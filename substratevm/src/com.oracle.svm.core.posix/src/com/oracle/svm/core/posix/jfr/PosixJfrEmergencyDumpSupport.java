package com.oracle.svm.core.posix.jfr;
import com.oracle.svm.core.SubstrateUtil;
import com.oracle.svm.core.headers.LibC;
import com.oracle.svm.core.posix.headers.Dirent;
import jdk.graal.compiler.word.Word;
import org.graalvm.nativeimage.c.type.CTypeConversion;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.nativeimage.Platform;
import org.graalvm.nativeimage.Platforms;

import com.oracle.svm.core.Uninterruptible;
import com.oracle.svm.core.handles.PrimitiveArrayView;
import com.oracle.svm.core.os.RawFileOperationSupport;
import com.oracle.svm.core.os.RawFileOperationSupport.FileAccessMode;
import com.oracle.svm.core.os.RawFileOperationSupport.FileCreationMode;
import com.oracle.svm.core.os.RawFileOperationSupport.RawFileDescriptor;


import jdk.graal.compiler.api.replacements.Fold;

import java.nio.charset.StandardCharsets;

import static com.oracle.svm.core.posix.headers.Fcntl.O_NOFOLLOW;
import static com.oracle.svm.core.posix.headers.Fcntl.O_RDONLY;
public class PosixJfrEmergencyDumpSupport implements com.oracle.svm.core.jfr.JfrEmergencyDumpSupport {
    private static final int CHUNK_FILE_HEADER_SIZE = 68;// TODO based on jdk file
    private static final int JVM_MAXPATHLEN = 4096;// TODO based on jdk file
    private String dumpPath;
    private CCharPointer cCharDumpPath;
    private CCharPointer defaultCCharDumpPath;
    private CCharPointer repositoryLocation;
    private CTypeConversion.CCharPointerHolder cCharDumpPathHolder;
    private CTypeConversion.CCharPointerHolder defaultCCharDumpPathHolder;
    private CTypeConversion.CCharPointerHolder repositoryLocationHolder;
    private RawFileDescriptor fd;
    private PrimitiveArrayView pathBuffer;
    private byte[] chunkfileExtensionBytes;

    private Directory directory;

    @Platforms(Platform.HOSTED_ONLY.class)
    public PosixJfrEmergencyDumpSupport() {
        directory = new Directory();
        chunkfileExtensionBytes = ".jfr".getBytes(StandardCharsets.UTF_8); // TODO double check its utf8 you want.
    }

    public void setRepositoryLocation(String dirText) {
        repositoryLocationHolder = CTypeConversion.toCString(dirText);
        repositoryLocation = repositoryLocationHolder.get();
    }

    public void setDumpPath(String dumpPathText) { // *** call this from beginRecordingOperation to set a default to CWD
        dumpPath = dumpPathText;
        // *** currently this is only called on OOME, but we could hardcode for other errors as well

        cCharDumpPathHolder = CTypeConversion.toCString(dumpPath + "hs_oom_pid_" + ProcessHandle.current().pid() + ".jfr");// TODO may need to add  "/" delimiter
        cCharDumpPath = cCharDumpPathHolder.get(); // This can allocate so do it eagerly.

        defaultCCharDumpPathHolder = CTypeConversion.toCString("hs_oom_pid_" + ProcessHandle.current().pid() + ".jfr");// TODO may need to add  "/" delimiter
        defaultCCharDumpPath = defaultCCharDumpPathHolder.get();

        pathBuffer =  PrimitiveArrayView.createForReadingAndWriting(new byte[JVM_MAXPATHLEN]);
        //TODO need to terminate the string with 0. otherwise length function will not work.
    }

    public String getDumpPath() {
        return dumpPath;
    }

    /** See JfrEmergencyDump::on_vm_error*/
    public void onVmError(){
        if (openEmergencyDumpFile()) {
            writeEmergencyDumpFile();
            getFileSupport().close(fd);
            fd = Word.nullPointer();
        }
    }

    private boolean openEmergencyDumpFile(){

        fd = getFileSupport().create(cCharDumpPath, FileCreationMode.CREATE, FileAccessMode.READ_WRITE); //gives us O_CREAT | O_RDWR for creation mode and S_IREAD | S_IWRITE permissions
        if (!getFileSupport().isValid(fd)) {
            // Fallback. Try to create it in the current directory.
            fd = getFileSupport().create(defaultCCharDumpPath, FileCreationMode.CREATE, FileAccessMode.READ_WRITE);
        }
        return getFileSupport().isValid(fd);
    }

    private void writeEmergencyDumpFile() {
        if (openDirectorySecure()) {
            if (directory == null) {
                return;
            }
            Dirent.dirent entry;
            while ((entry = Dirent.readdir(directory.dir)).isNonNull()) {
                if (filter(entry.d_name())){
                    String name = CTypeConversion.toJavaString(entry.d_name());
                    System.out.println("chunk file name: "+ name);
                }
            }
        }
    }

    // *** copied from PosixPerfMemoryProvider
    private boolean openDirectorySecure() {
        int fd = restartableOpen(repositoryLocation, O_RDONLY() | O_NOFOLLOW(), 0);
        if (fd == -1) {
            return false;
        }

//        if (!isDirFdSecure(fd)) { //TODO do we need this?
//            com.oracle.svm.core.posix.headers.Unistd.NoTransitions.close(fd);
//            return null;
//        }

        Dirent.DIR dir = Dirent.fdopendir(fd);
        if (dir.isNull()) {
            com.oracle.svm.core.posix.headers.Unistd.NoTransitions.close(fd);
            return false;
        }
        this.directory.set(fd, dir);
        return true;
    }

    // *** copied from PosixPerfMemoryProvider
    @Uninterruptible(reason = "LibC.errno() must not be overwritten accidentally.")
    private static int restartableOpen(CCharPointer directory, int flags, int mode) {
        int result;
        do {
            result = com.oracle.svm.core.posix.headers.Fcntl.NoTransitions.open(directory, flags, mode);
        } while (result == -1 && LibC.errno() == com.oracle.svm.core.posix.headers.Errno.EINTR());

        return result;
    }

    private boolean filter(CCharPointer fn){

        int filenameLength = (int) SubstrateUtil.strlen(fn).rawValue();
        // check filename extension

        if (filenameLength <= chunkfileExtensionBytes.length){
            return false;
        }
//        System.out.println("extension length " +chunkfileExtensionBytes.length);
//        System.out.println("fn length " + filenameLength);

        // Verify file extension
        for (int i = 0; i < chunkfileExtensionBytes.length; i++) {
            int idx1 = chunkfileExtensionBytes.length - i - 1;
            int idx2 = filenameLength - i - 1;
//            System.out.println("expected byte " + chunkfileExtensionBytes[idx1] + " actual byte " + ((org.graalvm.word.Pointer) fn).readByte(idx2));
            if (chunkfileExtensionBytes[idx1] != ((org.graalvm.word.Pointer) fn).readByte(idx2)) {
                System.out.println("failed ext check");
                return false;
            }
        }

        // Verify if you can open it and receive a valid file descriptor
        RawFileDescriptor chunkFd = getFileSupport().open(fullyQualified(fn) ,FileAccessMode.READ_WRITE);
        if (!getFileSupport().isValid(chunkFd)) {
            System.out.println("failed open check");
            return false;
        }

        // Verify file size
        long chunkFileSize = getFileSupport().size(chunkFd);
        if (chunkFileSize < CHUNK_FILE_HEADER_SIZE) {
            System.out.println("failed size check");
            return false;
        }

        return true;
    }

    private CCharPointer fullyQualified(CCharPointer fn){
        int fnLength =  + SubstrateUtil.strlen(fn).rawValue() ; // TODO add delimiter
        int repositoryLocationLength =  + SubstrateUtil.strlen(repositoryLocation).rawValue() ;

        for (int i = 0; i < repositoryLocationLength; i++) {
            getPathBuffer().write(i, repositoryLocation.read(i));
        }
        // TODO add delimiter
        for (int i = 0; i < fnLength; i++) {
            getPathBuffer().write(repositoryLocationLength + i, fn.read(i));
        }
        return getPathBuffer();
    }

    private CCharPointer getPathBuffer(){
        return (CCharPointer) pathBuffer.addressOfArrayElement(0);
    }

    @Fold
    static RawFileOperationSupport getFileSupport() {
        return RawFileOperationSupport.bigEndian();
    }

    public void teardown() {
        cCharDumpPathHolder.close(); // *** this must survive as long as we need the dump path c pointer.
        defaultCCharDumpPathHolder.close();
        repositoryLocationHolder.close();
        directory.close();
        pathBuffer.close();
    }

    // *** copied from PosixPerfMemoryProvider
    private static class Directory {
        private int fd;
        private Dirent.DIR dir;

        public void set(int fd, Dirent.DIR dir) {
            this.fd = fd;
            this.dir = dir;
        }

        public void close() {
            /* Close the directory (and implicitly the file descriptor). */
            Dirent.closedir(dir);
        }
    }
}

@com.oracle.svm.core.feature.AutomaticallyRegisteredFeature
class PosixJfrEmergencyDumpFeature extends com.oracle.svm.core.jfr.JfrEmergencyDumpFeature {

    @Override
    public void afterRegistration(AfterRegistrationAccess access) {
        PosixJfrEmergencyDumpSupport support = new PosixJfrEmergencyDumpSupport();
        org.graalvm.nativeimage.ImageSingletons.add(com.oracle.svm.core.jfr.JfrEmergencyDumpSupport.class, support);
        org.graalvm.nativeimage.ImageSingletons.add(PosixJfrEmergencyDumpSupport.class, support);
    }
}

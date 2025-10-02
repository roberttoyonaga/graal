package com.oracle.svm.hosted.code;

public class CalleeInfo {
    public double bc;
    public int depth;
    public com.oracle.svm.hosted.meta.HostedMethod method;
    public CalleeInfo (double bc, int depth, com.oracle.svm.hosted.meta.HostedMethod method) {
        this.bc = bc;
        this.depth = depth;
        this.method = method;
    }
}

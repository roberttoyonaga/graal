package com.oracle.svm.hosted.code;

public class CalleeInfo {
    public int sizeBeforeInlining; // This depends on the root graph, not the callee at all.
    public int lastRoundUpdated; // This is needed for dealing with multiple callsites.
    public com.oracle.svm.hosted.meta.HostedMethod method;
    public CalleeInfo ( com.oracle.svm.hosted.meta.HostedMethod method, int lastRoundUpdated) {
        this.method = method;
        this.lastRoundUpdated = lastRoundUpdated;
    }
}

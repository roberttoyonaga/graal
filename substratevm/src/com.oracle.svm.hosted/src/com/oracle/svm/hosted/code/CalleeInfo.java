package com.oracle.svm.hosted.code;

public class CalleeInfo {
    public double bc;
    public int depth;
    public int sizeBeforeInlining; // This depends on the root graph, not the callee at all.
    public int lastRoundUpdated; // This is needed for dealing with multiple callsites.
    public com.oracle.svm.hosted.meta.HostedMethod method;
    public boolean ignore; // We've already evaluated this callee for inlining and decided it wasn't worth it.
    public boolean secondLevel;
    public CalleeInfo (double bc, int depth, com.oracle.svm.hosted.meta.HostedMethod method, boolean secondLevel, int lastRoundUpdated) {
        this.bc = bc;
        this.depth = depth;
        this.method = method;
        this.secondLevel = secondLevel;
        this.lastRoundUpdated = lastRoundUpdated;

    }
}

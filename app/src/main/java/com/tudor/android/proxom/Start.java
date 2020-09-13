package com.tudor.android.proxom;;

public class Start {
    public static void main(String[] args) {
        try {
            ProxyThread proxyThread = new ProxyThread();
            BroadcastingThread broadcastingThread = new BroadcastingThread();
            proxyThread.startThread(args[0]);
            broadcastingThread.startThread();
            Thread.sleep(Long.MAX_VALUE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

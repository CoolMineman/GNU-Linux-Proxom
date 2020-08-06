package com.tudor.android.proxom;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.io.File;
import java.io.PrintStream;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BroadcastingThread extends Thread {
    private ScheduledExecutorService runningScheduledThread = null;

    private DatagramPacket broadcastingPacket = null;
    private DatagramSocket broadcastingSocket = null;
    private final int BROADCASTING_PORT = 47777;
    private final String BROADCASTING_ADDRESS_STRING = "255.255.255.255";
    private InetAddress broadcastingAddress = null;
    private String broadcastingMessage = "Server~Open~1~";
    private byte buffMessage[] = null;

    File writingFile = null;
    PrintStream writingStream = null;

    public void startThread(){
        writingFile = new File ("/sdcard/ProxomBroadcastingLog.txt");
        try {
            writingStream = new PrintStream(writingFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        writingStream.println("Errors:");

        try {
            broadcastingSocket = new DatagramSocket();
            broadcastingSocket.setBroadcast(true);
        } catch (SocketException e) {
            writingStream.println("Could not create the socket");
        }

        char aux[] = new char[2];
        aux[0] = 4;
        aux[1] = 2;
        buffMessage = new String(String.valueOf(aux) + broadcastingMessage).getBytes();

        try {
            broadcastingAddress = InetAddress.getByName(BROADCASTING_ADDRESS_STRING);
        } catch (UnknownHostException e) {
            writingStream.println("Could not create the broadcasting address");
        }

        broadcastingPacket = new DatagramPacket(buffMessage, buffMessage.length, broadcastingAddress, BROADCASTING_PORT);

        runningScheduledThread = Executors.newScheduledThreadPool(1);
        runningScheduledThread.scheduleAtFixedRate(this, 0, 1, TimeUnit.SECONDS);

        ProxomService.setBroadcastingStatus(true);
    }

    @Override
    public void run(){
        try {
            broadcastingSocket.send(broadcastingPacket);
        } catch (IOException e) {
            writingStream.println("Could not send the packet");
        }

    }

    public void stopThread(){
        runningScheduledThread.shutdown();
        ProxomService.setBroadcastingStatus(false);
    }

}

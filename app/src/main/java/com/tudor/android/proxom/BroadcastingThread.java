package com.tudor.android.proxom;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.io.File;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class BroadcastingThread extends Thread {
    private Thread runningThread = null;
    private volatile boolean running = false;

    private DatagramPacket broadcastingPacket = null;
    private DatagramSocket broadcastingSocket = null;
    private final int broadcastingPort = 47777;
    private final String broadcastingAddressString = "255.255.255.255";
    private InetAddress broadcastingAddress = null;
    private String broadcastingMessage = "Server~Open~1~";
    private byte buffMessage[] = null;

    File writingFile = null;
    PrintStream writingStream = null;

    public void startThread(){
        running = true;

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
            broadcastingAddress = InetAddress.getByName(broadcastingAddressString);
        } catch (UnknownHostException e) {
            writingStream.println("Could not create the broadcasting address");
        }

        broadcastingPacket = new DatagramPacket(buffMessage, buffMessage.length, broadcastingAddress, broadcastingPort);

        runningThread = new Thread(this);
        runningThread.start();
    }

    @Override
    public void run(){

        ProxomService.setBroadcastingStatus(true);

        while (running){
            try {
                broadcastingSocket.send(broadcastingPacket);
            } catch (IOException e) {
                writingStream.println("Could not send the packet");
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        ProxomService.setBroadcastingStatus(false);
    }

    public void stopThread(){
        running = false;
    }

}

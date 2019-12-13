package ru.bmstu.parallel.lab7;

import org.zeromq.SocketType;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;

import java.util.Scanner;

public class Client {

    private static final String SOCKET_ADDRESS = "tcp://localhost:8081";
    private static final int THREADS_NUM = 1;
    private static final int NO_FLAGS = 0;

    public static void main(String[] args) {
        ZMQ.Context context = ZMQ.context(THREADS_NUM);
        ZMQ.Socket requester = context.socket(SocketType.REQ);
        requester.connect(SOCKET_ADDRESS);
        Scanner in = new Scanner(System.in);
        System.out.println("Socket connected");

        while (!Thread.currentThread().isInterrupted()) {
            String line = in.nextLine();
            ZFrame frame  = new ZFrame(line);
            frame.send(requester, NO_FLAGS);
            String reply = requester.recvStr(NO_FLAGS);
            System.out.println("Got reply: " + reply);
        }
    }
}

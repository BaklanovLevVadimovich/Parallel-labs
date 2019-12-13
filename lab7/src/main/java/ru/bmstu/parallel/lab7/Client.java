package ru.bmstu.parallel.lab7;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;

import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket requester = context.socket(SocketType.REQ);
        requester.connect("tcp://localhost:8081");
        requester.setHWM(0);
        Scanner in = new Scanner(System.in);
        System.out.println("Socket connected");

        ZMQ.Poller items = context.poller(1);
        items.register(requester, ZMQ.Poller.POLLIN);
        while (!Thread.currentThread().isInterrupted()) {
            String line = in.nextLine();
            requester.send(line, 0);
            System.out.println("send line: " + line);
            String reply = requester.recvStr(0);
            System.out.println("Got reply: " + reply);
        }
    }
}

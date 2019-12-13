package ru.bmstu.parallel.lab7;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        ZContext context = new ZContext();
        ZMQ.Socket requester = context.createSocket(SocketType.REQ);
        requester.connect("tcp://localhost:8081");
        Scanner in = new Scanner(System.in);
        System.out.println("Socket connected");

        while (!Thread.currentThread().isInterrupted()) {
            String line = in.nextLine();
            requester.send(line, 0);
            System.out.println("send line: " + line);
            String reply = requester.recvStr(0);
            System.out.println("Got reply: " + reply);
        }
    }
}

package ru.bmstu.parallel.lab7;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;

import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket requester = context.socket(SocketType.REQ);
        requester.connect("tcp://localhost:5559");
        System.out.println("Socket connected");
        Scanner in = new Scanner(System.in);
        while (!Thread.currentThread().isInterrupted()) {
            String line = in.nextLine();
            System.out.println("readed line: " + line);
            requester.send(line, 0);
            System.out.println("send line: " + line);
            String reply = requester.recvStr();
            System.out.println("Got reply: " + reply);
        }
    }
}

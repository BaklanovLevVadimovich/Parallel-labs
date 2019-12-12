package ru.bmstu.parallel.lab7;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;

import java.util.Scanner;

public class Client {

    private static final String REQUEST_DELIMITER = " ";

    public static void main(String[] args) {
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket requester = context.socket(SocketType.REQ);
        requester.connect("tcp://localhost:5559");
        System.out.println("Socket connected");
        Scanner in = new Scanner(System.in);
        for (;;) {
            String line = in.nextLine();
//            String[] lineSplitted = line.split(REQUEST_DELIMITER);
//            String requestType = lineSplitted[0];
//            int cellNum = Integer.parseInt(lineSplitted[1]);
            requester.send()
        }
    }
}

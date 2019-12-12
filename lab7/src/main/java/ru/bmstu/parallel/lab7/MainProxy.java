package ru.bmstu.parallel.lab7;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;

public class MainProxy {

    private static final String REQUEST_DELIMITER = " ";

    public static void main(String[] args) {
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket clientWorker = context.socket(SocketType.ROUTER);
        ZMQ.Socket storeWorker = context.socket(SocketType.ROUTER);
        clientWorker.bind("tcp://*:5559");
        storeWorker.bind("tcp://*:5560");
        System.out.println("Launched proxy");
        ZMQ.Poller items = context.poller(2);
        items.register(clientWorker, ZMQ.Poller.POLLIN);
        boolean more = false;
        String message;
        while (!Thread.currentThread().isInterrupted()) {
            items.poll();
            if (items.pollin(0)) {
                while (true) {
                    message = clientWorker.recvStr(0);
                    System.out.println(message);
                    String[] lineSplitted = message.split(REQUEST_DELIMITER);
                    String requestType = lineSplitted[0];
                    int cellNum = Integer.parseInt(lineSplitted[1]);
                    System.out.println("Type: " + requestType + " | num: " + cellNum);
                    more = clientWorker.hasReceiveMore();
                    storeWorker.send(message, more ? ZMQ.SNDMORE : 0);
                    if (!more) {
                        break;
                    }
                }
            }
            if (items.pollin(1)) {
                while (true) {
                    message = storeWorker.recvStr();
                    more = clientWorker.hasReceiveMore();
                    storeWorker.send(message, more ? ZMQ.SNDMORE : 0);
                    if (!more) {
                        break;
                    }
                }
            }

        }
    }
}

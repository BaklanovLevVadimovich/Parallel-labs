package ru.bmstu.parallel.lab7;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;

import java.util.ArrayList;
import java.util.List;

public class MainProxy {

    private static final String REQUEST_DELIMITER = " ";
    private static List<String> clientIds = new ArrayList<>();
    private static List<String> storeIds = new ArrayList<>();

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
        byte[] message;
        while (!Thread.currentThread().isInterrupted()) {
            items.poll();
            if (items.pollin(0)) {
                while (true) {
                    System.out.println("GETTING NEW MESSAGE");
                    String id = clientWorker.recvStr();
                    if (isNewClient(id)) {
                        clientIds.add(id);
                    }
                    System.out.println(id);
                    clientWorker.sendMore(id);
                    message = clientWorker.recv(0);
                    System.out.println(new String(message));
//                    String[] lineSplitted = message.split(REQUEST_DELIMITER);
//                    String requestType = lineSplitted[0];
//                    int cellNum = Integer.parseInt(lineSplitted[1]);
//                    System.out.println("Type: " + requestType + " | num: " + cellNum);
//                    more = clientWorker.hasReceiveMore();
//                    storeWorker.send(message, more ? ZMQ.SNDMORE : 0);
//                    if (!more) {
//                        break;
//                    }
                }
            }
            if (items.pollin(1)) {
                while (true) {
                    message = storeWorker.recv();
                    more = clientWorker.hasReceiveMore();
                    storeWorker.send(message, more ? ZMQ.SNDMORE : 0);
                    if (!more) {
                        break;
                    }
                }
            }

        }
    }

    private static boolean isNewClient(String id) {
        for (int i = 0; i < clientIds.size(); i++) {
            if (clientIds.get(i).equals(id)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isNewStore(String id) {
        for (int i = 0; i < storeIds.size(); i++) {
            if (storeIds.get(i).equals(id)) {
                return false;
            }
        }
        return true;
    }
}

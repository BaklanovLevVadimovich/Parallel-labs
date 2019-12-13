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
        items.register(storeWorker, ZMQ.Poller.POLLIN);
        boolean more = false;
        String message;
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
                    clientWorker.recvStr();
                    message = clientWorker.recvStr(0);
                    System.out.println(message);
                    if (message.contains("get")) {
                        System.out.println("SEND GET REQUEST TO DATA STORE");
                        storeWorker.send(message, 0);
                        storeWorker.recvStr()
                    } else {

                    }
                }
            }
            if (items.pollin(1)) {
                while (true) {
                    message = storeWorker.recvStr();
                    System.out.println("GETTING NEW STORE MESSAGE");
                    String id = clientWorker.recvStr();
                    if (isNewStore(id)) {
                        storeIds.add(id);
                    }
                    System.out.println(id);
                    clientWorker.sendMore(id);
                    clientWorker.recvStr();
                    message = clientWorker.recvStr(0);

//                    more = clientWorker.hasReceiveMore();
//                    storeWorker.send(message, more ? ZMQ.SNDMORE : 0);
//                    if (!more) {
//                        break;
//                    }
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

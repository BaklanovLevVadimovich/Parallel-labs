package ru.bmstu.parallel.lab7;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;

import java.util.ArrayList;
import java.util.List;

public class MainProxy {

    private static final String REQUEST_DELIMITER = " ";
    private static final String STORE_MESSAGE_DELIMITER = "|";
    private static List<String> clientIds = new ArrayList<>();
    private static List<String> storeIds = new ArrayList<>();
    private static List<DataStoreInfo> storeInfos = new ArrayList<>();
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
//                        storeWorker.recvStr();
                    } else {

                    }
                }
            }
            if (items.pollin(1)) {
                while (true) {
                    System.out.println("GETTING NEW STORE MESSAGE");
                    String id = storeWorker.recvStr();
                    if (isNewStore(id)) {
                        DataStoreInfo info = new DataStoreInfo();
                        info.setId(id);
                        storeInfos.add(info);
                    }
                    System.out.println(id);
                    storeWorker.sendMore(id);
                    storeWorker.recvStr();
                    message = clientWorker.recvStr(0);
                    System.out.println("GOT MES FROM STORE " + message);
                    if (message.con)
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
        for (int i = 0; i < storeInfos.size(); i++) {
            if (storeInfos.get(i).getId().equals(id)) {
                return false;
            }
        }
        return true;
    }
}

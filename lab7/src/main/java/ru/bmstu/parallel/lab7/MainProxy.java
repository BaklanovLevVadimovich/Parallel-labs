package ru.bmstu.parallel.lab7;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;

import java.util.ArrayList;
import java.util.List;

public class MainProxy {

    private static final String REQUEST_DELIMITER = " ";
    private static final String STORE_RANGE_DELIMITER = "-";
    private static final String STORE_MESSAGE_DELIMITER = "/";
    private static List<String> clientIds = new ArrayList<>();
    private static List<DataStoreInfo> storeInfos = new ArrayList<>();

    public static void main(String[] args) {
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket clientWorker = context.socket(SocketType.ROUTER);
        ZMQ.Socket storeWorker = context.socket(SocketType.ROUTER);
        clientWorker.bind("tcp://*:8081");
        storeWorker.bind("tcp://*:8082");
        ZMQ.Poller items = context.poller(2);
        items.register(clientWorker, ZMQ.Poller.POLLIN);
        items.register(storeWorker, ZMQ.Poller.POLLIN);
        System.out.println("Launched proxy");
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
                    System.out.println("id:" + id);
                    clientWorker.sendMore(id);
                    clientWorker.recvStr();
                    message = clientWorker.recvStr();
                    System.out.println(message);
                    clientWorker.sendMore("");
                    clientWorker.send("roflan", 3);
                    System.out.println("Sended roflan");
                    if (message.contains("get")) {
                        String[] messageParts = message.split(REQUEST_DELIMITER);
                        String storeId = getDataStoreIdContainingCell(Integer.parseInt(messageParts[1]));
                        System.out.println("SEND GET REQUEST TO DATA STORE " + storeId);
//                        storeWorker.send(message, 0);
                        storeWorker.sendMore(storeId);
                        storeWorker.sendMore("");
                        System.out.println("SEND MORE PASSED");
                        storeWorker.send(message + " " + id, 0);
                        System.out.println("LAST SEND PASSED");
                    } else {

                    }
                    more = clientWorker.hasReceiveMore();
//                    storeWorker.send(message, more ? ZMQ.SNDMORE : 0);
                    if (!more) {
                        break;
                    }
                }
            }
            if (items.pollin(1)) {
                while (true) {
                    System.out.println("GETTING NEW STORE MESSAGE");
                    String id = storeWorker.recvStr(0);
                    if (isNewStore(id)) {
                        DataStoreInfo info = new DataStoreInfo();
                        info.setId(id);
                        storeInfos.add(info);
                    }
                    System.out.println("id: " + id);
//                    String delim = storeWorker.recvStr();
//                    System.out.println("delim:" + delim);
                    message = storeWorker.recvStr(0);
//                    storeWorker.send(id, 0);
                    storeWorker.sendMore(id);
                    storeWorker.sendMore("");
                    storeWorker.send("ping", 0);
                    System.out.println("GOT MES FROM STORE " + message);
                    String[] messageParts = message.split(STORE_MESSAGE_DELIMITER);
                    if (messageParts[0].equals("NOTIFY")) {
                        String[] rangeParts = messageParts[1].split(STORE_RANGE_DELIMITER);
                        int rangeStart = Integer.parseInt(rangeParts[0]);
                        int rangeEnd = Integer.parseInt(rangeParts[1]);
                        setNewDataStoreInfo(id, rangeStart, rangeEnd);
                    }
                    if (messageParts[0].equals("VALUE")) {
                        clientWorker.sendMore(messageParts[2]);
                        clientWorker.send(messageParts[1], 0);
                    }
                    if (messageParts[0].equals("UPDATE")) {

                    }
                    more = storeWorker.hasReceiveMore();
//                    storeWorker.send(message, more ? ZMQ.SNDMORE : 0);
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
        for (int i = 0; i < storeInfos.size(); i++) {
            if (storeInfos.get(i).getId().equals(id)) {
                return false;
            }
        }
        return true;
    }

    private static void setNewDataStoreInfo(String id, int rangeStart, int rangeEnd) {
        for (int i = 0; i < storeInfos.size(); i++) {
            DataStoreInfo currentInfo = storeInfos.get(i);
            if (currentInfo.getId().equals(id)) {
                currentInfo.setBeginRange(rangeStart);
                currentInfo.setEndRange(rangeEnd);
            }
        }
    }

    private static String getDataStoreIdContainingCell(int cellNum) {
        for (int i = 0;  i < storeInfos.size(); i++) {
            DataStoreInfo currentInfo = storeInfos.get(i);
            if (cellNum >= currentInfo.getBeginRange() && cellNum <= currentInfo.getEndRange()) {
                return currentInfo.getId();
            }
        }
        return "";
    }
}

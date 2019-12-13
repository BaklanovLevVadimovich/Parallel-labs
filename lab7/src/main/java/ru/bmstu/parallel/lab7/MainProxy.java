package ru.bmstu.parallel.lab7;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;

import java.util.ArrayList;
import java.util.List;

public class MainProxy {

    private static final String REQUEST_DELIMITER = " ";
    private static final String STORE_RANGE_DELIMITER = "-";
    private static final String STORE_MESSAGE_DELIMITER = "|";
    private static List<String> clientIds = new ArrayList<>();
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
                        String[] messageParts = message.split(REQUEST_DELIMITER);
                        String storeId = getDataStoreIdContainingCell(Integer.parseInt(messageParts[1]));
                        System.out.println("SEND GET REQUEST TO DATA STORE " + storeId);
                        storeWorker.sendMore(storeId);
                        storeWorker.send(message, 0);
//                        storeWorker.recvStr();
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
                    String id = storeWorker.recvStr();
                    if (isNewStore(id)) {
                        DataStoreInfo info = new DataStoreInfo();
                        info.setId(id);
                        storeInfos.add(info);
                    }
                    System.out.println("id: " + id);
                    storeWorker.sendMore(id);
//                    String delim = storeWorker.recvStr();
//                    System.out.println("delim:" + delim);
                    message = storeWorker.recvStr(0);
                    System.out.println("GOT MES FROM STORE " + message);
                    String[] messageParts = message.split(STORE_MESSAGE_DELIMITER);
                    System.out.println("part0: " + messageParts[0]);
                    System.out.println("part1: " + messageParts[1]);
                    System.out.println("part@: " + messageParts[2]);
                    if (messageParts[0].equals("NOTIFY")) {
                        String[] rangeParts = messageParts[1].split(STORE_RANGE_DELIMITER);
                        int rangeStart = Integer.parseInt(rangeParts[0]);
                        int rangeEnd = Integer.parseInt(rangeParts[1]);
                        System.out.println("GOT RANGES: " + String.valueOf(rangeStart) + " " + String.valueOf(rangeEnd));
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
                System.out.println("FOUND AND SET NEW RANGES");
                currentInfo.setBeginRange(rangeStart);
                currentInfo.setEndRange(rangeEnd);
            }
        }
    }

    private static String getDataStoreIdContainingCell(int cellNum) {
        for (int i = 0;  i < storeInfos.size(); i++) {
            DataStoreInfo currentInfo = storeInfos.get(i);
            System.out.println("CELL NUM: " + String.valueOf(cellNum));
            System.out.println("UPPER: " + String.valueOf(currentInfo.getEndRange()));
            System.out.println("LOWER: " + String.valueOf(currentInfo.getBeginRange()));
            System.out.println("ID: " + currentInfo.getId());
            if (cellNum >= currentInfo.getBeginRange() && cellNum <= currentInfo.getEndRange()) {
                return currentInfo.getId();
            }
        }
        return "";
    }
}

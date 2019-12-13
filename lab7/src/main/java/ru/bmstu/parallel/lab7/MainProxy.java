package ru.bmstu.parallel.lab7;

import org.zeromq.SocketType;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainProxy {

    private static final String REQUEST_DELIMITER = " ";
    private static final String STORE_RANGE_DELIMITER = "-";
    private static final String STORE_MESSAGE_DELIMITER = "/";
    private static List<byte[]> clientIds = new ArrayList<>();
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
                    ZMsg msg = ZMsg.recvMsg(clientWorker);
                    System.out.println(msg.toString());
                    byte[] id = msg.getFirst().getData();
                    if (isNewClient(id)) {
                        clientIds.add(id);
                    }
                    message = msg.getLast().toString();
                    System.out.println("GOT MESSAGE FROM CLIENT: " + msg.toString());
                    if (message.contains("get")) {
                        String[] messageParts = message.split(REQUEST_DELIMITER);
                        int cellNum = Integer.parseInt(messageParts[1]);
                        byte[] storeId = getDataStoreIdContainingCell(cellNum);
                        ZMsg storeMsg = new ZMsg();
                        storeMsg.add(new ZFrame(storeId));
                        storeMsg.add(new ZFrame(message));
                        storeMsg.add(new ZFrame(id));
                        System.out.println("SEND GET REQUEST TO DATA STORE: " + storeMsg.toString());
                        storeMsg.send(storeWorker);
                    } else {
                        if (message.contains("put")) {
                            String[] messageParts = message.split(REQUEST_DELIMITER);
                            int cellNum = Integer.parseInt(messageParts[1]);
                            List<byte[]> storeIds = getAllDataStoreIdsContainingCell(cellNum);
                            System.out.println("STORES NUM " + String.valueOf(storeIds.size()));
                            for (int i = 0; i < storeIds.size(); i++) {
                                System.out.println(i);
                                System.out.println(storeIds.get(i));
                                ZMsg storeMsg = new ZMsg();
                                storeMsg.addFirst(new ZFrame(storeIds.get(i)));
                                storeMsg.add(new ZFrame(message));
                                storeMsg.send(storeWorker);
                            }
                            msg.getLast().reset("Updated");
                            msg.send(clientWorker);
                        } else {
                            msg.getLast().reset("Unknown command");
                            msg.send(clientWorker);
                        }
                    }
                    more = clientWorker.hasReceiveMore();
                    if (!more) {
                        break;
                    }
                }
            }
            if (items.pollin(1)) {
                while (true) {
                    ZMsg msg = ZMsg.recvMsg(storeWorker);
                    System.out.println("GOT MES FROM STORE " + msg.toString());
                    byte[] id = msg.getFirst().getData();
                    if (isNewStore(id)) {
                        DataStoreInfo info = new DataStoreInfo();
                        info.setId(id);
                        storeInfos.add(info);
                    }
                    msg.pop();
                    message = msg.getFirst().toString();
                    String[] messageParts = message.split(STORE_MESSAGE_DELIMITER);
                    if (messageParts[0].equals("NOTIFY")) {
                        String[] rangeParts = messageParts[1].split(STORE_RANGE_DELIMITER);
                        int rangeStart = Integer.parseInt(rangeParts[0]);
                        int rangeEnd = Integer.parseInt(rangeParts[1]);
                        setNewDataStoreInfo(id, rangeStart, rangeEnd);
                    }
                    if (messageParts[0].equals("VALUE")) {
                        ZMsg clientMsg = new ZMsg();
                        clientMsg.add(new ZFrame(msg.getLast().getData()));
                        clientMsg.add(new ZFrame(""));
                        clientMsg.add(new ZFrame(messageParts[1]));
                        clientMsg.send(clientWorker, false);
                        System.out.println("Send res to client: " + clientMsg.toString());
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

    private static boolean isNewClient(byte[] id) {
        for (int i = 0; i < clientIds.size(); i++) {
            if (Arrays.equals(clientIds.get(i), id)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isNewStore(byte[] id) {
        for (int i = 0; i < storeInfos.size(); i++) {
            if (Arrays.equals(storeInfos.get(i).getId(), id)) {
                return false;
            }
        }
        return true;
    }

    private static void setNewDataStoreInfo(byte[] id, int rangeStart, int rangeEnd) {
        for (int i = 0; i < storeInfos.size(); i++) {
            DataStoreInfo currentInfo = storeInfos.get(i);
            if (Arrays.equals(currentInfo.getId(), id)) {
                currentInfo.setRangeStart(rangeStart);
                currentInfo.setRangeEnd(rangeEnd);
            }
        }
    }

    private static byte[] getDataStoreIdContainingCell(int cellNum) {
        for (int i = 0;  i < storeInfos.size(); i++) {
            DataStoreInfo currentInfo = storeInfos.get(i);
            if (cellNum >= currentInfo.getRangeStart() && cellNum <= currentInfo.getRangeEnd()) {
                return currentInfo.getId();
            }
        }
        return new byte[0];
    }

    private static List<byte[]> getAllDataStoreIdsContainingCell(int cellNum) {
        List<byte[]> result = new ArrayList<>();
        for (int i = 0; i < storeInfos.size(); i++) {
            DataStoreInfo currentInfo = storeInfos.get(i);
            if (cellNum >= currentInfo.getRangeStart() && cellNum <= currentInfo.getRangeEnd()) {
                result.add(currentInfo.getId());
            }
        }
        return result;
    }
}

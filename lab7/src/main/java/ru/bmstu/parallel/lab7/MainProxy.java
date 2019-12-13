package ru.bmstu.parallel.lab7;

import org.zeromq.SocketType;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

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
                    System.out.println("GETTING NEW MESSAGE");
                    ZMsg msg = ZMsg.recvMsg(clientWorker);
                    System.out.println(msg.toString());
                    byte[] id = msg.getFirst().getData();
                    if (isNewClient(id)) {
                        clientIds.add(id);
                    }
                    message = msg.getLast().toString();
                    System.out.println("id: " + id);
                    System.out.println("message: " + message);
                    if (message.contains("get")) {
                        String[] messageParts = message.split(REQUEST_DELIMITER);
                        byte[] storeId = getDataStoreIdContainingCell(Integer.parseInt(messageParts[1]));
                        System.out.println("SEND GET REQUEST TO DATA STORE");
                        ZMsg storeMsg = new ZMsg();
                        storeMsg.add(new ZFrame(storeId));
                        storeMsg.add(new ZFrame(message + " " + id));
                        storeMsg.send(storeWorker);
//                        storeWorker.sendMore(storeId);
//                        storeWorker.sendMore("");
//                        System.out.println("SEND MORE PASSED");
//                        storeWorker.send(message + " " + id, 0);
                        System.out.println("LAST SEND PASSED");
                    } else {

                    }
                    more = clientWorker.hasReceiveMore();
                    if (!more) {
                        break;
                    }
                }
            }
            if (items.pollin(1)) {
                while (true) {
                    System.out.println("GETTING NEW STORE MESSAGE");
                    ZMsg msg = ZMsg.recvMsg(storeWorker);
                    byte[] id = msg.getFirst().getData();
                    if (isNewStore(id)) {
                        DataStoreInfo info = new DataStoreInfo();
                        info.setId(id);
                        storeInfos.add(info);
                    }
                    message = msg.getLast().toString();
                    System.out.println("id: " + id);
                    System.out.println("GOT MES FROM STORE " + message);
                    ZMsg storeMsg = new ZMsg();
                    storeMsg.add(new ZFrame(id));
                    storeMsg.add("ping");
                    System.out.println(storeMsg);
                    storeMsg.send(storeWorker);
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
                currentInfo.setBeginRange(rangeStart);
                currentInfo.setEndRange(rangeEnd);
            }
        }
    }

    private static byte[] getDataStoreIdContainingCell(int cellNum) {
        for (int i = 0;  i < storeInfos.size(); i++) {
            DataStoreInfo currentInfo = storeInfos.get(i);
            if (cellNum >= currentInfo.getBeginRange() && cellNum <= currentInfo.getEndRange()) {
                return currentInfo.getId();
            }
        }
        return new byte[0];
    }
}

package ru.bmstu.parallel.lab7;

import org.zeromq.SocketType;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MainProxy {

    private static final int THREADS_NUM = 1;
    private static final int POLLER_SIZE = 2;
    private static final String REQUEST_DELIMITER = " ";
    private static final String STORE_RANGE_DELIMITER = "-";
    private static final String STORE_MESSAGE_DELIMITER = "/";
    private static final String CLIENT_SOCKET_ADDRESS = "tcp://*:8081";
    private static final String STORE_SOCKET_ADDRESS = "tcp://*:8082";
    private static final String GET_COMMAND = "get";
    private static final String PUT_COMMAND = "put";
    private static final String UPDATED_MESSAGE = "UPDATED";
    private static final String UNKNOWN_COMMAND_MESSAGE = "Unknown command";
    private static final String NOTIFY_MESSAGE = "NOTIFY";
    private static final String GET_MESSAGE = "VALUE";
    private static final String PUT_MESSAGE = "UPDATE";
    private static final String FRAME_DELIMITER = "";
    private static Random random;
    private static List<byte[]> clientIds = new ArrayList<>();
    private static List<DataStoreInfo> storeInfos = new ArrayList<>();

    public static void main(String[] args) {
        ZMQ.Context context = ZMQ.context(THREADS_NUM);
        ZMQ.Socket clientWorker = context.socket(SocketType.ROUTER);
        ZMQ.Socket storeWorker = context.socket(SocketType.ROUTER);
        clientWorker.bind(CLIENT_SOCKET_ADDRESS);
        storeWorker.bind(STORE_SOCKET_ADDRESS);
        ZMQ.Poller items = context.poller(POLLER_SIZE);
        items.register(clientWorker, ZMQ.Poller.POLLIN);
        items.register(storeWorker, ZMQ.Poller.POLLIN);
        System.out.println("Launched proxy");
        boolean more = false;
        String message;
        while (!Thread.currentThread().isInterrupted()) {
            items.poll();
            if (items.pollin(0)) {
                while (true) {
                    ZMsg clientMessage = ZMsg.recvMsg(clientWorker);
                    System.out.println(clientMessage.toString());
                    byte[] clientId = clientMessage.getFirst().getData();
                    if (isNewClient(clientId)) {
                        clientIds.add(clientId);
                    }
                    message = clientMessage.getLast().toString();
                    System.out.println("GOT MESSAGE FROM CLIENT: " + clientMessage.toString());
                    String[] messageParts = message.split(REQUEST_DELIMITER);
                    String command = messageParts[0];
                    int cellNum = Integer.parseInt(messageParts[1]);
                    if (command.equals(GET_COMMAND)) {
                        byte[] storeId = getRandomStoreIdContainingCell(cellNum);
                        ZMsg storeMsg = new ZMsg();
                        storeMsg.add(new ZFrame(storeId));
                        storeMsg.add(new ZFrame(message));
                        storeMsg.add(new ZFrame(clientId));
                        System.out.println("SEND GET REQUEST TO DATA STORE: " + storeMsg.toString());
                        storeMsg.send(storeWorker);
                    } else {
                        if (command.equals(PUT_COMMAND)) {
                            List<byte[]> storeIds = getAllDataStoreIdsContainingCell(cellNum);
                            for (int i = 0; i < storeIds.size(); i++) {
                                ZMsg storeMsg = new ZMsg();
                                storeMsg.addFirst(new ZFrame(storeIds.get(i)));
                                storeMsg.add(new ZFrame(message));
                                storeMsg.send(storeWorker);
                            }
                            clientMessage.getLast().reset(UPDATED_MESSAGE);
                            clientMessage.send(clientWorker);
                        } else {
                            clientMessage.getLast().reset(UNKNOWN_COMMAND_MESSAGE);
                            clientMessage.send(clientWorker);
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
                    ZMsg storeMessage = ZMsg.recvMsg(storeWorker);
                    System.out.println("GOT MESSAGE FROM STORE " + storeMessage.toString());
                    byte[] storeId = storeMessage.getFirst().getData();
                    if (isNewStore(storeId)) {
                        DataStoreInfo info = new DataStoreInfo();
                        info.setId(storeId);
                        storeInfos.add(info);
                    }
                    storeMessage.pop();
                    message = storeMessage.getFirst().toString();
                    String[] messageParts = message.split(STORE_MESSAGE_DELIMITER);
                    String responseKey = messageParts[0];
                    if (responseKey.equals(NOTIFY_MESSAGE)) {
                        String[] rangeParts = messageParts[1].split(STORE_RANGE_DELIMITER);
                        int rangeStart = Integer.parseInt(rangeParts[0]);
                        int rangeEnd = Integer.parseInt(rangeParts[1]);
                        setNewDataStoreInfo(storeId, rangeStart, rangeEnd);
                    }
                    if (responseKey.equals(GET_MESSAGE)) {
                        ZMsg clientMsg = new ZMsg();
                        clientMsg.add(new ZFrame(storeMessage.getLast().getData()));
                        clientMsg.add(new ZFrame(FRAME_DELIMITER));
                        clientMsg.add(new ZFrame(messageParts[1]));
                        System.out.println("Send result to client: " + clientMsg.toString());
                        clientMsg.send(clientWorker);
                    }
                    more = storeWorker.hasReceiveMore();
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

    private static byte[] getRandomStoreIdContainingCell(int cellNum) {
        if (random == null) {
            random = new Random(System.currentTimeMillis());
        }
        List<byte[]> pool = new ArrayList<>();
        for (int i = 0;  i < storeInfos.size(); i++) {
            DataStoreInfo currentInfo = storeInfos.get(i);
            if (cellNum >= currentInfo.getRangeStart() && cellNum <= currentInfo.getRangeEnd()) {
                pool.add(currentInfo.getId());
            }
        }
        return pool.get(random.nextInt(pool.size()));
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

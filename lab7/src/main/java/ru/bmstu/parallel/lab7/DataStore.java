package ru.bmstu.parallel.lab7;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

import java.util.HashMap;

public class DataStore {

    private static HashMap<Integer, String> data = new HashMap<>();
    private static int rangeBegin;
    private static int rangeEnd;
    private static final String RANGE_DELIMITER = "-";
    private static final String VALUES_DELIMITER = ",";
    private static final String REQUEST_DELIMITER = " ";

    public static void main(String[] args) {
        String range = args[0];
        String values = args[1];
        String[] rangeSplitted = range.split(RANGE_DELIMITER);
        rangeBegin = Integer.parseInt(rangeSplitted[0]);
        rangeEnd = Integer.parseInt(rangeSplitted[1]);
        String[] valuesSplitted = values.split(VALUES_DELIMITER);
        for (int i = rangeBegin, j = 0; i <= rangeEnd; i++, j++) {
            data.put(i, valuesSplitted[j]);
        }

        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket socket = context.socket(SocketType.DEALER);
        socket.connect("tcp://localhost:8082");
        System.out.println("Socket connected");
        socket.send("NOTIFY/" + range + "/", 0);
        long lastNotifyTime = System.currentTimeMillis();
        while (true) {
            System.out.println("NEW MESSAGE");
//            ZMsg msg = ZMsg.recvMsg(socket);
            ZMsg msg = ZMsg.recvMsg(socket);
//            String message = socket.recvStr();
//            String message1 = socket.recvStr(0);
//            String message2 = socket.recvStr(0);
            System.out.println("GOT MESSAGE: " + msg.popString());
//            System.out.println("GOT MESSAGE: " + message1);
//            System.out.println("GOT MESSAGE: " + message2);
//            String[] messageParts = message.split(REQUEST_DELIMITER);
//            int cellNum = Integer.parseInt(messageParts[1]);
//            String clientId;
//            if (messageParts[0].equals("get")) {
//                clientId = messageParts[2];
//                socket.send("VALUE/" + data.get(cellNum) + "/" + clientId);
//            } else {
//                data.replace(cellNum, messageParts[2]);
//                clientId = messageParts[3];
//                socket.send("UPDATE/SUCCESS/" + clientId);
//            }
//            long currentTime = System.currentTimeMillis();
//            if (currentTime - lastNotifyTime > 10000) {
//                socket.send("NOTIFY/" + range + "/");
//                lastNotifyTime = currentTime;
//            }
        }


    }
}

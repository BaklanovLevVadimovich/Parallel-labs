package ru.bmstu.parallel.lab7;

import org.zeromq.SocketType;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

import java.util.HashMap;

public class DataStore {

    private static HashMap<Integer, String> data = new HashMap<>();
    private static int rangeBegin;
    private static int rangeEnd;
    private static final int THREADS_NUM = 1;
    private static final int NOTIFY_INTERVAL_MILLIS = 10000;
    private static final String RANGE_DELIMITER = "-";
    private static final String VALUES_DELIMITER = ",";
    private static final String REQUEST_DELIMITER = " ";
    private static final String RESPONSE_DELIMITER = "/";
    private static final String SOCKET_ADDRESS = "tcp://localhost:8082";
    private static final String GET_COMMAND = "get";
    private static final String NOTIFY_MESSAGE = "NOTIFY";
    private static final String GET_MESSAGE = "VALUE";
    private static final String PUT_MESSAGE = "UPDATE";
    private static final String SUCCESS_MESSAGE = "SUCCESS";

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

        ZMQ.Context context = ZMQ.context(THREADS_NUM);
        ZMQ.Socket socket = context.socket(SocketType.DEALER);
        socket.connect(SOCKET_ADDRESS);
        System.out.println("Socket connected");
        Thread notifyThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        socket.send(NOTIFY_MESSAGE + RESPONSE_DELIMITER + range, 0);
                        Thread.sleep(NOTIFY_INTERVAL_MILLIS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        notifyThread.start();
        System.out.println("started cycle");
        socket.send(NOTIFY_MESSAGE + RESPONSE_DELIMITER + range, 0);
        while (true) {
            System.out.println("started cycle");
            ZMsg zMsg = ZMsg.recvMsg(socket);
            String message = zMsg.getFirst().toString();
            System.out.println("GOT MESSAGE: " + message);
            String[] messageParts = message.split(REQUEST_DELIMITER); //get x || put x data
            String command = messageParts[0];
            int cellNum = Integer.parseInt(messageParts[1]);
            ZMsg resultMsg = new ZMsg();
            if (command.equals(GET_COMMAND)) {
                resultMsg.add(new ZFrame(GET_MESSAGE + RESPONSE_DELIMITER + data.get(cellNum)));
            } else {
                String newData = messageParts[2];
                data.replace(cellNum, newData);
                resultMsg.add(new ZFrame(PUT_MESSAGE + RESPONSE_DELIMITER + SUCCESS_MESSAGE));
            }
            resultMsg.add(new ZFrame(zMsg.getLast().getData()));
            System.out.println("SENDING MESSAGE FROM STORE: " + resultMsg.toString());
            resultMsg.send(socket);
        }


    }
}

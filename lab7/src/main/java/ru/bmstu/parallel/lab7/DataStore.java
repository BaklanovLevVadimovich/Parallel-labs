package ru.bmstu.parallel.lab7;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;

import java.util.HashMap;

public class DataStore {

    private static HashMap<Integer, String> data;
    private static int rangeBegin;
    private static int rangeEnd;
    private static final String RANGE_DELIMITER = "-";
    private static final String VALUES_DELIMITER = ",";

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
        socket.connect("tcp://localhost:5560");
        while(true) {
            
        }


    }
}

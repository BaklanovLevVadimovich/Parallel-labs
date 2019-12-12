package ru.bmstu.parallel.lab7;

import java.util.HashMap;

public class DataStore {

    private static HashMap<Integer, String> data;
    private static int rangeBegin;
    private static int rangeEnd;
    private static final String RANGE_DELIMETER = "-";
    private static final String VAlUES_DELIMETER = ",";

    public static void main(String[] args) {
        String range = args[0];
        String values = args[1];
        String[] rangeSplitted = range.split(RANGE_DELIMETER);
        rangeBegin = Integer.parseInt(rangeSplitted[0]);
        rangeEnd = Integer.parseInt(rangeSplitted[1]);
        String[] valuesSplitted = values.split(VAlUES_DELIMETER);
        for (int i = rangeBegin, j = 0; i <= rangeEnd; i++, j++) {
            data.put(i, valuesSplitted[j]);
        }
        
    }
}

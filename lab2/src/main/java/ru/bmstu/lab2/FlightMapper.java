package ru.bmstu.lab2;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.Optional;

public class FlightMapper extends Mapper<LongWritable, Text, JoinWritableComparable, Text> {

    private static final int FLIGHT_TYPE = 1;
    private static final int DEST_AIRPORT_INDEX = 14;
    private static final int DELAY_INDEX = 18;
    private static final int FIRSTLINE_INDEX = 0;
    private static final String COMMA = ",";

    @Override
    protected void map(LongWritable key, Text value, Mapper.Context context) throws IOException, InterruptedException {

        if (!key.equals(new LongWritable(FIRSTLINE_INDEX))) {
            String line = value.toString();
            String[] fields = line.split(COMMA);
            int destAirportId = Integer.parseInt(fields[DEST_AIRPORT_INDEX]);
            String delayStr = fields[DELAY_INDEX];
            if (!delayStr.isEmpty() && Float.parseFloat(delayStr) > 0) {
                JoinWritableComparable writableKey = new JoinWritableComparable(destAirportId, FLIGHT_TYPE);
                context.write(writableKey, new Text(delayStr));
            }
        }
    }

    public String getValidatedDelayString(String delayStr) {
        Optional<String> res = Optional.empty();
        res.
    }

}
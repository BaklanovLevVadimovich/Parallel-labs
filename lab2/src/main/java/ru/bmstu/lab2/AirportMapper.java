package ru.bmstu.lab2;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class AirportMapper extends Mapper<LongWritable, Text, JoinWritableComparable, Text> {

    private static final int AIRPORT_TYPE = 0;
    private static final int SPLIT_LIMIT = 2;
    private static final int ID_FIELD = 0;
    private static final int NAME_FIELD = 1;
    private static final int FIRSTLINE_INDEX = 0;
    private static final String QUOTE_STRING = "\"";
    private static final String COMMA_STRING = ",";
    private static final String EMPTY_STRING = "";

    @Override
    protected void map(LongWritable key, Text value, Mapper.Context context) throws IOException, InterruptedException {

        if (!key.equals(new LongWritable(FIRSTLINE_INDEX))) {
            String line = value.toString();
            line = removeQuotes(line);
            String[] fields = line.split(COMMA_STRING, SPLIT_LIMIT);
            int id = Integer.parseInt(fields[ID_FIELD]);
            String name = fields[NAME_FIELD];
            JoinWritableComparable writableKey = new JoinWritableComparable(id, AIRPORT_TYPE);
            context.write(writableKey, new Text(name));
        }
    }

    private String removeQuotes(String line) {
        return line.replace(QUOTE_STRING, EMPTY_STRING);
    }

}
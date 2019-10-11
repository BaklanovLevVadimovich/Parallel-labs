import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class AirportMapper extends Mapper<LongWritable, Text, JoinWritableComparable, Text> {

    private final int AIRPORT_TYPE = 0;

    @Override

    protected void map(LongWritable key, Text value, Mapper.Context context) throws IOException, InterruptedException {

        if (!key.equals(new LongWritable(0))) {
            String line = value.toString();
            line = line.replace("\"", "");
            String[] fields = line.split(",", 2);
            int id = Integer.parseInt(fields[0]);
            String name = fields[1];

            JoinWritableComparable writableKey = new JoinWritableComparable(id, AIRPORT_TYPE);
            context.write(writableKey, name);
        }
    }

}
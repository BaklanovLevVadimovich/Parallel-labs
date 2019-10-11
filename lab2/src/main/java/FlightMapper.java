import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class FlightMapper extends Mapper<LongWritable, Text, JoinWritableComparable, Text> {

    private final int FLIGHT_TYPE = 1;
    private final int DEST_AIRPORT_INDEX = 14;
    private final int DElAY_INDEX = 18;

    @Override

    protected void map(LongWritable key, Text value, Mapper.Context context) throws IOException, InterruptedException {

        if (!key.equals(new LongWritable(0))) {
            String line = value.toString();
            
        }
    }

}
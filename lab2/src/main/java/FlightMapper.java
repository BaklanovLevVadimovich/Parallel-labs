import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class FlightMapper extends Mapper<LongWritable, Text, JoinWritableComparable, Text> {

    private final int FLIGHT_TYPE = 1;
    private final int DEST_AIRPORT_INDEX = 14;
    private final int DELAY_INDEX = 18;

    @Override

    protected void map(LongWritable key, Text value, Mapper.Context context) throws IOException, InterruptedException {

        if (!key.equals(new LongWritable(0))) {
            String line = value.toString();
            line.replace("\"", "");
            String[] fields = line.split(",");
            int destAirportId = Integer.parseInt(fields[DEST_AIRPORT_INDEX]);
            String delayStr = fields[DELAY_INDEX];
            if (Float.parseFloat(delayStr) > 0) {
                JoinWritableComparable writableKey = new JoinWritableComparable(destAirportId, FLIGHT_TYPE);
                context.write(writableKey, delayStr);
            }
        }
    }

}
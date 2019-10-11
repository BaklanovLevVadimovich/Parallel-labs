import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Iterator;

public class JoinReducer extends Reducer<JoinWritableComparable, Text, Text, Text> {

    @Override
    protected void reduce(JoinWritableComparable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        Text airportName = new Text();
        boolean firstLineFlag = true;
        int flightsDelayed = 0;
        float delaySum = 0;
        float minDelay = -1;
        float maxDelay = -1;

        Iterator iter = values.iterator();
        for (iter.hasNext()) {
            if (firstLineFlag) {
                
            }
        }
    }

} 
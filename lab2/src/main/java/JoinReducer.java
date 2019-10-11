import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Iterator;

public class JoinReducer extends Reducer<JoinWritableComparable, Text, Text, Text> {

    @Override
    protected void reduce(JoinWritableComparable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

        
    }

} 
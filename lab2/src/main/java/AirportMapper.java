import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class AirportMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

    @Override

    protected void map(LongWritable key, Text value, Mapper.Context context) throws IOException, InterruptedException {

        String line = value.toString();
        line = line.replaceAll("[\\p{Punct}]", "");
        String[] words = line.split(" ");
        for (String word : words) {
            word = word.toLowerCase();
            context.write(new Text(word), new IntWritable(1));
        }
    }

}
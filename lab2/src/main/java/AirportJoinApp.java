
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class AirportJoinApp {

    public static void main(String[] args) throws Exception {

        if (args.length != 3) {

            System.err.println("Usage: AirportJoinApp <airports input path> <flights input path> <output path>");

            System.exit(-1);
        }


        Job job = Job.getInstance();

        job.setJarByClass(AirportJoinApp.class);

        job.setJobName("Airport Join");

        MultipleInputs.addInputPath(job, new Path(args[0]), TextInputFormat.class, AirportMapper.class);
        MultipleInputs.addInputPath(job, new Path(args[1]), TextInputFormat.class, FlightMapper.class);
        
        FileOutputFormat.setOutputPath(job, new Path(args[2]));

        job.setGroupingComparatorClass(JoinGroupingComparator.class);

        job.setPartitionerClass(JoinPartitioner.class);

        job.setReducerClass(JoinReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        System.exit(job.waitForCompletion(true) ? 0 : 1);

    }

} 
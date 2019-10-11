import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
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

        FileInputFormat.addInputPath(job, new Path(args[0]));

        FileOutputFormat.setOutputPath(job, new Path(args[2]));

//        job.setMapperClass(WordMapper.class);
//
//        job.setReducerClass(WordReducer.class);

        job.setOutputKeyClass(Text.class);

        job.setOutputValueClass(IntWritable.class);

        job.setNumReduceTasks(2);

        System.exit(job.waitForCompletion(true) ? 0 : 1);

    }

} 
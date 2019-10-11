import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.Partitioner;

public class JoinPartitioner implements Partitioner<JoinWritableComparable, Text> {
    @Override
    public int getPartition(JoinWritableComparable key, Text value, int reduceTasksNum) {
        return (key.getAirportId())
    }
}

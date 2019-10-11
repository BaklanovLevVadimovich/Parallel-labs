import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.Partitioner;

public class JoinPartitioner implements Partitioner<JoinWritableComparable, Text> {
    @Override
    public void getPartition(JoinWritableComparable key, Text value, int n) {

    }
}

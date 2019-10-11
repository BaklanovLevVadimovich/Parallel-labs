import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class JoinGroupingComparator extends WritableComparator {

    @Override
    public int compare(WritableComparable first, WritableComparable second) {
        return first.getAirportId() - second.getAirportId();
    }
}

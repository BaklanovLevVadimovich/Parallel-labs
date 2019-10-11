import org.apache.hadoop.io.WritableComparator;

public class JoinGroupingComparator extends WritableComparator {

    public int compare(JoinWritableComparable first, JoinWritableComparable second) {
        return first.getAirportId() - second.getAirportId();
    }
}

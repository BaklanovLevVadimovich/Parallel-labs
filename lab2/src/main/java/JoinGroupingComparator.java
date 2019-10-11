import org.apache.hadoop.io.WritableComparator;

public class JoinGroupingComparator extends WritableComparator {

    public int compare(Wr first, JoinWritableComparable second) {
        return first.getAirportId() - second.getAirportId();
    }
}

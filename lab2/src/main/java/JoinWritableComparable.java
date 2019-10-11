import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;

public class JoinWritableComparable implements WritableComparable<JoinWritableComparable> {

    private int airportId;
    private int dataType;

    @Override
    public void write(DataOutput output) {

    }

    @Override
    public void readFields(DataInput input) {

    }

    @Override
    public int compareTo(JoinWritableComparable another) {

    }

    public void setAirportId(int airportId) {
        this.airportId = airportId;
    }

    public int getAirportId() {
        return airportId;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }
}

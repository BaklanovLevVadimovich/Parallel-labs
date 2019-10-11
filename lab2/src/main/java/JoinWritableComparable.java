import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class JoinWritableComparable implements WritableComparable<JoinWritableComparable> {

    private int airportId;
    private int dataType;

    public JoinWritableComparable(int id, int type) {
        airportId = id;
        dataType = type;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeInt(airportId);
        output.writeInt(dataType);
    }

    @Override
    public void readFields(DataInput input) throws IOException{
        airportId = input.readInt();
        dataType = input.readInt();
    }

    @Override
    public int compareTo(JoinWritableComparable another) {
        if (airportId == another.getAirportId()) {
            if ()
        }
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

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class FlightWritable implements Writable {

    private int airportId;
    private float delay;

    public FlightWritable(int id, float delay) {
        airportId = id;
        this.delay = delay;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeInt(airportId);
        output.writeFloat(delay);
    }

    @Override
    public void readFields(DataInput input) throws IOException {
        airportId = input.readInt();
        delay = input.readFloat();
    }
}

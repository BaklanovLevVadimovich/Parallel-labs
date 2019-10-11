import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;

public class AirportWritable implements Writable {

    private int airportId;
    private float delay;

    public AirportWritable() {
        
    }

    @Override
    public void write(DataOutput output) {

    }

    @Override
    public void readFields(DataInput input) {

    }
}

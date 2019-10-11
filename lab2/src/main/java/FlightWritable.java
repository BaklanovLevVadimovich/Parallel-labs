import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;

public class FlightWritable implements Writable {

    private int 



    @Override
    public void write(DataOutput output) {
        output.writeInt();
    }

    @Override
    public void readFields(DataInput input) {

    }
}

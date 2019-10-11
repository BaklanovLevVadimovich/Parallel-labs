import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class FlightWritable implements Writable {

    private int airportId;

    private String airportName;



    @Override
    public void write(DataOutput output) {
        try {
            output.writeInt(airportId);
            output.writeUTF(airportName);
        } catch (IOException e) {
            System.out.println("FlightWritable write exception");
            e.printStackTrace();
        }
    }

    @Override
    public void readFields(DataInput input) {

    }

    public int getAirportId() {
        return airportId;
    }

    public String getAirportName() {
        return airportName;
    }

    public void setAirportId(int airportId) {
        this.airportId = airportId;
    }

    public void setAirportName(String airportName) {
        this.airportName = airportName;
    }
}

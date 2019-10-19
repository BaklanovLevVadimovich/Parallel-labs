package ru.bmstu.lab2;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Iterator;

public class JoinReducer extends Reducer<JoinWritableComparable, Text, Text, Text> {

    private static final int MIN_DELAY_DEFAULT_VALUE = -1;
    private static final int MAX_DELAY_DEFAULT_VALUE = -1;

    @Override
    protected void reduce(JoinWritableComparable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        Text airportName = new Text();
        boolean firstLineFlag = true;
        int flightsDelayed = 0;
        float delaySum = 0;
        float averageDelay = 0;
        float minDelay = MIN_DELAY_DEFAULT_VALUE;
        float maxDelay = MAX_DELAY_DEFAULT_VALUE;

        float currentDelay;

        Iterator iter = values.iterator();
        while (iter.hasNext()) {
            if (firstLineFlag) {
                airportName.set((Text)iter.next());
                firstLineFlag = false;
            } else {
                currentDelay = Float.parseFloat((iter.next()).toString());
                if (currentDelay > 0) {
                    if (minDelay == MIN_DELAY_DEFAULT_VALUE || currentDelay < minDelay) minDelay = currentDelay;

                    if (maxDelay == MAX_DELAY_DEFAULT_VALUE || currentDelay > maxDelay) maxDelay = currentDelay;

                    delaySum += currentDelay;
                    flightsDelayed++;
                }
            }
        }

        averageDelay = delaySum/flightsDelayed;

        if (flightsDelayed > 0) {
            context.write(new Text(String.valueOf(key.getAirportId()) + " | " + airportName + ": "), new Text("average delay = " + String.format("%.2f", averageDelay) +
                    " , min delay = " + String.format("%.2f", minDelay) + " , max delay = " + String.format("%.2f", maxDelay)));
        }
    }

} 
package ru.bmstu.lab3;

import java.io.Serializable;

public class FlightStat implements Serializable {
    private int flightCount;

    private int flightsDelayed;

    private int flightsCancelled;

    private float maxDelay;

    public FlightStat(int flightCount, int flightsDelayed, int flightsCancelled, float maxDelay) {
        this.flightCount = flightCount;
        this.flightsDelayed = flightsDelayed;
        this.flightsCancelled = flightsCancelled;
        this.maxDelay = maxDelay;
    }

    public static FlightStat addValue(FlightStat a, FlightStat b) {

    }


}

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

    public static FlightStat addValue(FlightStat a, boolean isDelayed, boolean isCancelled, float delayTime) {
        return new FlightStat(
                a.getFlightCount() + 1,
                isDelayed ? a.getFlightsDelayed() + 1 : a.getFlightsDelayed(),
                isCancelled ? a.getFlightsCancelled() + 1 : a.getFlightsCancelled(),
                delayTime > a.getMaxDelay() ? delayTime : a.getMaxDelay());
    }

    public static FlightStat add(FlightStat a, FlightStat b) {
        return new FlightStat(
                a.getFlightCount() + b.getFlightCount(),
                a.getFlightsDelayed() + b.getFlightsDelayed(),
                a.getFlightsCancelled() + b.getFlightsCancelled(),
                a.getMaxDelay() > b.getMaxDelay() ? a.getMaxDelay() : b.getMaxDelay());
    }

    @Override
    public String toString() {
        return "Flights delayed: " + String.valueOf((float)flightsDelayed/(float)flightCount * 100) +
                "% Flights cancelled: " + String.valueOf((float)flightsCancelled/(float)flightCount * 100) +
                "%  Maximum delay: " + String.valueOf(maxDelay);
    }

    public float getMaxDelay() {
        return maxDelay;
    }

    public int getFlightCount() {
        return flightCount;
    }

    public int getFlightsCancelled() {
        return flightsCancelled;
    }

    public int getFlightsDelayed() {
        return flightsDelayed;
    }

    public void setFlightCount(int flightCount) {
        this.flightCount = flightCount;
    }

    public void setFlightsCancelled(int flightsCancelled) {
        this.flightsCancelled = flightsCancelled;
    }

    public void setFlightsDelayed(int flightsDelayed) {
        this.flightsDelayed = flightsDelayed;
    }

    public void setMaxDelay(float maxDelay) {
        this.maxDelay = maxDelay;
    }
}

package ru.bmstu.lab3;

import java.io.Serializable;

public class Flight implements Serializable {

    private int originAirportId;

    private int destAirportId;

    private boolean isCancelled;

    private float delay;

    public Flight(int )


    public boolean isCancelled() {
        return isCancelled;
    }

    public float getDelay() {
        return delay;
    }

    public int getDestAirportId() {
        return destAirportId;
    }

    public int getOriginAirportId() {
        return originAirportId;
    }

    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

    public void setDelay(float delay) {
        this.delay = delay;
    }

    public void setDestAirportId(int destAirportId) {
        this.destAirportId = destAirportId;
    }

    public void setOriginAirportId(int originAirportId) {
        this.originAirportId = originAirportId;
    }
}

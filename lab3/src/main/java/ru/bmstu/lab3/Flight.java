package ru.bmstu.lab3;

import java.io.Serializable;

public class Flight implements Serializable {

    private boolean isCancelled;

    private float delay;

    public Flight(boolean isCancelled, float delay) {
        this.isCancelled = isCancelled;
        this.delay = delay;
    }


    public boolean isCancelled() {
        return isCancelled;
    }

    public float getDelay() {
        return delay;
    }

    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

    public void setDelay(float delay) {
        this.delay = delay;
    }
}

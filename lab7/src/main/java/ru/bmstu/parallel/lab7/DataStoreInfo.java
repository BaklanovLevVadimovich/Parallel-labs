package ru.bmstu.parallel.lab7;

public class DataStoreInfo {

    private byte[] id;

    private int rangeStart;

    private int rangeEnd;

    public int getRangeStart() {
        return rangeStart;
    }

    public int getRangeEnd() {
        return rangeEnd;
    }

    public byte[] getId() {
        return id;
    }

    public void setRangeStart(int rangeStart) {
        this.rangeStart = rangeStart;
    }

    public void setRangeEnd(int rangeEnd) {
        this.rangeEnd = rangeEnd;
    }

    public void setId(byte[] id) {
        this.id = id;
    }
}

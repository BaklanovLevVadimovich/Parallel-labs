package ru.bmstu.parallel.lab7;

public class DataStoreInfo {

    private String id;

    private int beginRange;

    private int endRange;

    public int getBeginRange() {
        return beginRange;
    }

    public int getEndRange() {
        return endRange;
    }

    public String getId() {
        return id;
    }

    public void setBeginRange(int beginRange) {
        this.beginRange = beginRange;
    }

    public void setEndRange(int endRange) {
        this.endRange = endRange;
    }

    public void setId(String id) {
        this.id = id;
    }
}

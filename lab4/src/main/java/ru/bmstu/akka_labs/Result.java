package ru.bmstu.akka_labs;

import java.util.ArrayList;

public class Result {
    private int packageId;
    private ArrayList<Test> tests;

    public Result(int packageId, ArrayList<Test> tests) {
        this.packageId = packageId;
        this.tests = tests;
    }

    public int getPackageId() {
        return packageId;
    }

    public ArrayList<Test> getTests() {
        return tests;
    }

    public void setPackageId(int packageId) {
        this.packageId = packageId;
    }

    public void setTests(ArrayList<Test> tests) {
        this.tests = tests;
    }
}

package ru.bmstu.akka_labs;

import java.util.ArrayList;

public class Result {
    private ArrayList<Test> tests;

    public Result(ArrayList<Test> tests) {
        this.tests = tests;
    }

    public ArrayList<Test> getTests() {
        return tests;
    }

    public void setTests(ArrayList<Test> tests) {
        this.tests = tests;
    }
}

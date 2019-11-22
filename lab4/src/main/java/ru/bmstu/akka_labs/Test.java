package ru.bmstu.akka_labs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Test {
    private final String name;
    private final String expectedResult;
    private final Object[] params;
    private boolean success;

    @JsonCreator
    Test(@JsonProperty("testName") String name,
         @JsonProperty("expectedResult") String expectedResult, Object[] params) {
        this.name = name;
        this.expectedResult = expectedResult;
        this.params = params;
    }

    public String getExpectedResult() {
        return expectedResult;
    }

    public Object[] getParams() {
        return params;
    }

    public String getName() {
        return name;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isSuccessful() {
        return success;
    }
}

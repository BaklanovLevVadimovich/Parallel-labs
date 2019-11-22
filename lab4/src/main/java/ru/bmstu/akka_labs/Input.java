package ru.bmstu.akka_labs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Input {
    private final int packageId;
    private final String jsScript;
    private final String functionName;
    private final Test[] tests;

    @JsonCreator
    Input(@JsonProperty("packageId") int packageId,
          @JsonProperty("jsScript") String jsScript,
          @JsonProperty("functionName") String functionName,
          @JsonProperty("tests") Test[] tests) {
        this.packageId = packageId;
        this.jsScript = jsScript;
        this.functionName = functionName;
        this.tests = tests;
    }

    public int getPackageId() {
        return packageId;
    }

    public String getFunctionName() {
        return functionName;
    }

    public String getJsScript() {
        return jsScript;
    }

    public Test[] getTests() {
        return tests;
    }
}

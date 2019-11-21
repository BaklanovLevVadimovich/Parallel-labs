package ru.bmstu.akka_labs;

public class Input {
    private final int packageId;
    private final String jsScript;
    private final String functionName;
    private final Test[] tests;

    public Input(int packageId, String jsScript, String functionName, Test[] tests) {
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

package ru.bmstu.akka_labs;

public class SingleTestInput {
    private final int packageId;
    private final String jsScript;
    private final String functionName;
    private final Test test;

    public SingleTestInput(int packageId, String jsScript, String functionName, Test test) {
        this.packageId = packageId;
        this.jsScript = jsScript;
        this.functionName = functionName;
        this.test = test;
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

    public Test getTest() {
        return test;
    }
}

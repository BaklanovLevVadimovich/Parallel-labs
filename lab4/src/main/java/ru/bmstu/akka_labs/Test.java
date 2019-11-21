package ru.bmstu.akka_labs;

public class Test {
    private final String name;
    private final Object expectedResult;
    private final Object[] params;

    public Test(String name, Object expectedResult, Object[] params) {
        this.name = name;
        this.expectedResult = expectedResult;
        this.params = params;
    }
}

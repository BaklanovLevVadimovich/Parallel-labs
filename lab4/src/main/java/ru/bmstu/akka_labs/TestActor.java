package ru.bmstu.akka_labs;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class TestActor extends AbstractActor {

    private ActorRef storage;

    public TestActor(ActorRef storage) {
        this.storage = storage;
    }

    private static String runTest(SingleTestInput input) throws ScriptException, NoSuchMethodException {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        engine.eval(input.getJsScript());
        Invocable invocable = (Invocable) engine;
        return invocable.invokeFunction(input.getFunctionName(), input.getTest().getParams()).toString();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SingleTestInput.class, m -> {
                    String result = runTest(m);
                    boolean passed = result.equals(m.getTest().getExpectedResult());
                    if (passed) {
                        System.out.println("Test " + m.getTest().getName() + " passed");
                    } else {
                        System.out.println("Test " + m.getTest().getName() + " failed(EXPECTED " + m.getTest().getExpectedResult() + ", GOT " + result + ")");
                    }
                    m.getTest().setSuccess(passed);
                    if (storage == null) System.out.println("storage null");
                    if (m.getTest() == null) System.out.println("test null");
                    if (getSelf() == null) System.out.println("self null");
                    storage.tell(m.getTest(), getSelf());
                })
                .build();
    }
}

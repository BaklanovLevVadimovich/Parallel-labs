package ru.bmstu.akka_labs;

import akka.actor.AbstractActor;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class TestActor extends AbstractActor {

    private String runTest(SingleTestInput input) throws ScriptException, NoSuchMethodException {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        engine.eval(input.getJsScript());
        Invocable invocable = (Invocable) engine;
        return invocable.invokeFunction(input.getFunctionName(), input.getTest().getParams()).toString();
    }

    private boolean isTestSuccessful(String )

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SingleTestInput.class, m -> {

                })
    }
}

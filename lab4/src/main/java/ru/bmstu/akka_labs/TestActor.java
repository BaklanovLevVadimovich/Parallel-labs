package ru.bmstu.akka_labs;

import akka.actor.AbstractActor;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class TestActor extends AbstractActor {

    private String runTest() {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        engine.eval(jscript);
        Invocable invocable = (Invocable) engine;
        return invocable.invokeFunction(functionName, params).toString();

    }

    @Override
    public Receive createReceive() {

    }
}

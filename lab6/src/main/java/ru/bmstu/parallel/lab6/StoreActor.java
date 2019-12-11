package ru.bmstu.parallel.lab6;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;

import java.util.HashMap;
import java.util.Map;

public class StoreActor extends AbstractActor {

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(GetRandomServerMessage.class, m -> saveResult(m))
                .match(SetServersMessage.class, m -> sender().tell(getRandomServer(), getSelf()))
                .build();
    }

    private String getRandomServer() {
        
    }
}
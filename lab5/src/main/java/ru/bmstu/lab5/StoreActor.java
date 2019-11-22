package ru.bmstu.akka_labs;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;
import ru.bmstu.lab5.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StoreActor extends AbstractActor {

    public static final long RESPONCE_TIME_DEFAULT_VALUE = -1;

    private Map<String, Long> store = new HashMap<>();

    private Long getResult(String url) {
        if (store.containsKey(url)) {
            System.out.println("Already got result for " + url);
            return store.get(url);
        } else {
            System.out.println("No result for " + url);
            return RESPONCE_TIME_DEFAULT_VALUE;
        }
    }

    private void saveResult(Result result) {
        store.put(result.getUrl(), result.getResponseTime());
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(Result.class, m -> saveResult(m))
                .match(String.class, m -> sender().tell(getResult(m), getSelf()))
                .build();
    }
}

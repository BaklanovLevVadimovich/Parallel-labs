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
            return store.get(result.getUrl());
        } else {
            System.out.println("No result for " + result.getUrl());
            return RESPONCE_TIME_DEFAULT_VALUE;
        }
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(SingleTestInput.class, m -> addTest(m.getPackageId(), m.getTest()))
                .match(String.class, m -> sender().tell(new Result(store.getOrDefault(Integer.parseInt(m), new ArrayList<>())), getSelf()))
                .build();
    }
}

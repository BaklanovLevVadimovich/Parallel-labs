package ru.bmstu.akka_labs;

import akka.actor.AbstractActor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StoreActor extends AbstractActor {

    private Map<Integer, ArrayList<Test>> store = new HashMap<>();

    private void addTest(Integer packageId, Test test) {
        if (store.containsKey(packageId)) {
            store.get(packageId).add(test);
        } else {
            ArrayList<Test> tests = new ArrayList<>();
            tests.add(test);
            store.put(packageId, tests);
        }
    }

    @Override
    public Receive createReceive() {
        
    }
}

package ru.bmstu.akka_labs;

import akka.actor.AbstractActor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StoreActor extends AbstractActor {

    private Map<Integer, ArrayList<Test>> store = new HashMap<>();

    @Override
    public Receive createReceive() {

    }
}

package ru.bmstu.akka_labs;

import akka.actor.AbstractActor;

public class RouteActor extends AbstractActor {

    private static final int ACTORS_NUM = 5;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match()
    }
}

package ru.bmstu.akka_labs;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;

public class RouteActor extends AbstractActor {

    private static final int ACTORS_NUM = 5;
    private ActorRef worker;
    private ActorRef storage;

    public RouteActor() {
        storage = 
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match()
    }
}

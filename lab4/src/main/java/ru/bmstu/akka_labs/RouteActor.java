package ru.bmstu.akka_labs;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.routing.RoundRobinPool;

public class RouteActor extends AbstractActor {

    private static final int ACTORS_POOL = 5;
    private ActorRef worker;
    private ActorRef storage;

    public RouteActor() {
        storage = getContext().actorOf(new RoundRobinPool(ACTORS_POOL))
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match()
    }
}

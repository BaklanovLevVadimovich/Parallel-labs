package ru.bmstu.akka_labs;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.routing.RoundRobinPool;

public class RouteActor extends AbstractActor {

    private static final int ACTORS_NUM = 5;
    private ActorRef worker;
    private ActorRef storage;

    public RouteActor() {
        storage = getContext().actorOf(new RoundRobinPool(ACTORS_NUM))
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match()
    }
}

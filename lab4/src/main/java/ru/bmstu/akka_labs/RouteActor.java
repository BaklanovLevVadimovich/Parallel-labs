package ru.bmstu.akka_labs;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.routing.RoundRobinPool;

public class RouteActor extends AbstractActor {

    private static final int ACTORS_POOL = 5;
    private ActorRef worker;
    private ActorRef storage;

    public RouteActor() {
        storage = getContext().actorOf(new RoundRobinPool(ACTORS_POOL).props(Props.create(StoreActor)))
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match()
    }
}

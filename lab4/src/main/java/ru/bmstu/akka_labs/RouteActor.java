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
        storage = getContext().actorOf(Props.create(StoreActor.class));
        worker = getContext().actorOf(new RoundRobinPool(ACTORS_POOL).props(Props.create(TestActor.class, storage)));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Input.class, m -> {
                    for (int i = 0; i < m.getTests().length; i++) {
                        worker.tell(new SingleTestInput(m.getPackageId(), m.getJsScript(), m.getFunctionName(), m.getTests()[i]), getSelf());
                    }
                })
                .match(String.class, m -> storage.tell(m, sender()))
                .build();
    }
}

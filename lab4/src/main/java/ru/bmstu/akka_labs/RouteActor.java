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
        worker = getContext().actorOf(new RoundRobinPool(ACTORS_POOL).props(Props.create(TestActor.class, storage)));
        storage = getContext().actorOf(Props.create(StoreActor.class));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Input.class, m -> {
                    for (int i = 0; i < m.getTests().length; i++) {
                        worker.tell(new SingleTestInput(m.getPackageId(), m.getJsScript(), m.getFunctionName(), m.getTests()[i]), getSelf());
                    }
                })
                .match(String, m -> storage.tell(m, getSelf()))
                .build();
    }
}

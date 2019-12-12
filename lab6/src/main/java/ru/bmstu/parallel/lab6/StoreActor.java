package ru.bmstu.parallel.lab6;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;

import java.util.*;

public class StoreActor extends AbstractActor {

    private List<String> servers;
    private Random random;

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(GetRandomServerMessage.class, m -> sender().tell(getRandomServer(), getSelf()))
                .match(SetServersMessage.class, m -> setServers(m.getServers()))
                .build();
    }

    private String getRandomServer() {
        System.out.println("GET RANDOM SERVER");
        if (random == null) {
            random = new Random(System.currentTimeMillis());
        }
        String server = servers.get(random.nextInt(servers.size()));
        System.out.println("Redirecting to " + server);
        return server;
    }

    private void setServers(List<String> servers) {
        System.out.println("New server list set");
        this.servers = servers;
    }
}
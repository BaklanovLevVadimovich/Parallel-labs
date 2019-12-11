package ru.bmstu.parallel.lab6;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class StoreActor extends AbstractActor {

    private ArrayList<String> servers;
    private Random random;

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(GetRandomServerMessage.class, m -> sender().tell(getRandomServer(), getSelf()))
                .match(SetServersMessage.class, m -> setServers(m.getServers()))
                .build();
    }

    private String getRandomServer() {
        if (random == null) {
            random = new Random()
        }
        String server =
    }

    private void setServers(ArrayList<String> servers) {
        System.out.println("New server list set");
        this.servers = servers;
    }
}
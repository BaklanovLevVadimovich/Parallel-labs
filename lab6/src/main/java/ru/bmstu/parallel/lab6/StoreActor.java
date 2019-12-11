package ru.bmstu.parallel.lab6;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StoreActor extends AbstractActor {

    private ArrayList<String> servers;

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(GetRandomServerMessage.class, m -> sender().tell(getRandomServer(), getSelf()))
                .match(SetServersMessage.class, m -> setServers(m.getServers()))
                .build();
    }

    private String getRandomServer() {

    }

    private void setServers(ArrayList<String> servers) {
        this.servers = servers;
    }
}
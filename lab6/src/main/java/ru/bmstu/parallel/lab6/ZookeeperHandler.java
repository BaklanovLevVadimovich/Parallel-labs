package ru.bmstu.parallel.lab6;

import akka.actor.ActorRef;
import org.apache.zookeeper.ZooKeeper;

public class ZookeeperHandler {

    private ActorRef storageActor;
    private ZooKeeper zoo;
    private static final String connectString = "localhost:2081";
    private static final int TIMEOUT_MILLIS = 5000;

    public ZookeeperHandler(ActorRef storageActor) {
        this.storageActor = storageActor;
        zoo = new ZooKeeper(connectString, TIMEOUT_MILLIS, null);
    }
}

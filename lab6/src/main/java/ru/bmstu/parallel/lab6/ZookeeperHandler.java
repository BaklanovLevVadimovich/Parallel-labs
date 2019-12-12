package ru.bmstu.parallel.lab6;

import akka.actor.ActorRef;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.List;

public class ZookeeperHandler {

    private ActorRef storageActor;
    private ZooKeeper zoo;
    private static final String connectString = "localhost:2081";
    private static final String HOST = "localhost";
    private static final String SERVERS_PATH = "/servers";
    private static final int TIMEOUT_MILLIS = 5000;

    public ZookeeperHandler(ActorRef storageActor) throws IOException {
        this.storageActor = storageActor;
        zoo = new ZooKeeper(connectString, TIMEOUT_MILLIS, null);
        watchServersUpdate();
    }

    public void createServer(int port) throws KeeperException, InterruptedException {
        String name = HOST + ":" + String.valueOf(port);
        String serverPath = zoo.create(
            SERVERS_PATH + "/" + name,
                name.getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL);
        System.out.println("Created server: " + serverPath);
    }

    private void setServers(SetServersMessage msg) {
        storageActor.tell(msg, ActorRef.noSender());
    }

    private void watchServersUpdate() {
        try {
            List<String> servers = zoo.getChildren(SERVERS_PATH, event -> {
                System.out.println("Zookeeper event: " + event.toString());
                watchServersUpdate();
            });
            
        } catch (KeeperException | InterruptedException e) {
            throw  new RuntimeException(e);
        }
    }
}

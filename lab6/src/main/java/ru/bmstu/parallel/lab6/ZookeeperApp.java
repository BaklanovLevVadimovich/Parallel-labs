package ru.bmstu.parallel.lab6;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import org.apache.zookeeper.KeeperException;
import org.asynchttpclient.AsyncHttpClient;

import java.io.IOException;
import java.util.concurrent.CompletionStage;

import static org.asynchttpclient.Dsl.asyncHttpClient;

public class ZookeeperApp{

    private static final String HOST = "localhost";

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        System.out.println("started");
        int port = Integer.parseInt(args[0]);
        ActorSystem system = ActorSystem.create("routes");
        final Http http = Http.get(system);
        final AsyncHttpClient asyncHttpClient = asyncHttpClient();
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        ActorRef storeActor = system.actorOf(Props.create(StoreActor.class));
        Server server = new Server(asyncHttpClient, http, storeActor, port);
        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = server.createRoute().flow(system, materializer);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(routeFlow, ConnectHttp.toHost(HOST, port), materializer);
        System.out.println("Server online at localhost:" + String.valueOf(port));
        System.in.read();
        binding.thenCompose(ServerBinding::unbind).thenAccept(unbound -> system.terminate());
    }

}

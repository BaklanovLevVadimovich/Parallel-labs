package ru.bmstu.parallel.lab6;

import akka.actor.ActorRef;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;
import akka.pattern.PatternsCS;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.util.concurrent.CompletionStage;

import static akka.http.javadsl.server.Directives.*;

public class Server {

    private static final String URL_PARAMETER_NAME = "url";
    private static final String COUNT_PARAMETER_NAME = "count";
    private static final int TIMEOUT_MILLIS = 15000;
    private ActorRef storeActor;
    private Http http;

    public Server(Http http, ActorRef storeActor, int port) throws IOException, KeeperException, InterruptedException {
        this.http = http;
        this.storeActor = storeActor;
        ZookeeperHandler zookeeperHandler = new ZookeeperHandler(storeActor);
        zookeeperHandler.createServer(port);
    }

    public Route createRoute() {
        return route(
                get(() ->
                        parameter(URL_PARAMETER_NAME,
                                url -> parameter(COUNT_PARAMETER_NAME,
                                        count -> {
                                            System.out.println("Route: " + url + " " + count);
                                            int countInt = Integer.parseInt(count);
                                            if (countInt > 0) {
                                                 return completeWithFuture(redirect(url, countInt));
                                            } else {
                                                return completeWithFuture(sendRequest(url));
                                            }
                                        })
                        )
                )
        );
    }

    private CompletionStage<HttpResponse> sendRequest(String url) {
        System.out.println("send request " + url);
        return http.singleRequest(HttpRequest.create(url));
    }

    private CompletionStage<HttpResponse> redirect(String url, int count) {
        return PatternsCS.ask(storeActor, new GetRandomServerMessage(), TIMEOUT_MILLIS)
                .thenCompose(serverUrl ->
                        sendRequest("http://" + serverUrl + "/?url=" + url + "&count=" + String.valueOf(count-1)));
    }
}

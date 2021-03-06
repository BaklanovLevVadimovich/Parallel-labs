package ru.bmstu.akka_labs;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import scala.concurrent.Future;

import java.io.IOException;
import java.util.concurrent.CompletionStage;

import static akka.http.javadsl.server.Directives.*;

public class Server {
    private static final String RESULT_PATH = "result";
    private static final String RUN_PATH = "run";
    private static final int TIMEOUT_MILLIS = 5000;

    public static void main(String[] args) throws IOException {
        ActorSystem system = ActorSystem.create("routes");
        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        Server instance = new Server();
        ActorRef router = system.actorOf(Props.create(RouteActor.class));
        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = instance.createRoute(router).flow(system, materializer);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(routeFlow, ConnectHttp.toHost("localhost", 8081), materializer);
        System.out.println("Server online at localhost:8081");
        System.in.read();
        binding.thenCompose(ServerBinding::unbind).thenAccept(unbound -> system.terminate());
    }

    private Route createRoute(ActorRef router) {
        return route(
                path(RESULT_PATH, () ->
                        route(
                                get(() ->
                                        parameter("packageId", id -> {
                                            Future<Object> result = Patterns.ask(router, id, TIMEOUT_MILLIS);
                                            return completeOKWithFuture(result, Jackson.marshaller());
                                        }))
                        )
                ),
                path(RUN_PATH, () ->
                        route(
                                post(() -> entity(Jackson.unmarshaller(Input.class), msg -> {
                                    router.tell(msg, ActorRef.noSender());
                                    return complete("Started test\n");
                                }) )
                        ))
        );
    }
}

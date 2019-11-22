package ru.bmstu.lab5;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.japi.Pair;
import akka.pattern.Patterns;
import akka.pattern.PatternsCS;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import scala.concurrent.Future;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletionStage;

public class StreamsApp {

    private static final int TIMEOUT_MILLIS = 5000;
    private static ActorRef storeActor;

    public static void main(String[] args) throws IOException {
        ActorSystem system = ActorSystem.create("routes");
        storeActor = system.actorOf(Props.create(StoreActor.class));
        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = createFlow(http, system, materializer);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(routeFlow, ConnectHttp.toHost("localhost", 8081), materializer);
        System.out.println("Server online at localhost:8081");
        System.in.read();
        binding.thenCompose(ServerBinding::unbind).thenAccept(unbound -> system.terminate());
    }

    private static Flow<HttpRequest, HttpResponse, NotUsed> createFlow(Http http, ActorSystem system, ActorMaterializer materializer) {
        Flow.of(HttpRequest.class)
                .map(request -> {
                    Map<String, String> params = request.getUri().query().toMap();
                    String url = params.get("testUrl");
                    int count = Integer.parseInt(params.get("count"));
                    return new Pair<>(url, count);
                })
                .mapAsync(4, pair -> {
                    CompletionStage<Object> result = PatternsCS.ask(storeActor, pair.first(), TIMEOUT_MILLIS);
                    result.thenCompose(res -> {
                        if ((Long)res != -1) {
                            return 
                        } else {

                        }
                    })
                })
    }
}

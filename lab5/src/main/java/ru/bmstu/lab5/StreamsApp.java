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
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import static org.asynchttpclient.Dsl.asyncHttpClient;

public class StreamsApp {

    private static final int TIMEOUT_MILLIS = 5000;
    private static ActorRef storeActor;

    public static void main(String[] args) throws IOException {
        ActorSystem system = ActorSystem.create("routes");
        storeActor = system.actorOf(Props.create(StoreActor.class));
        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = createFlow(materializer);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(routeFlow, ConnectHttp.toHost("localhost", 8081), materializer);
        System.out.println("Server online at localhost:8081");
        System.in.read();
        binding.thenCompose(ServerBinding::unbind).thenAccept(unbound -> system.terminate());
    }

    private static Flow<HttpRequest, HttpResponse, NotUsed> createFlow(ActorMaterializer materializer) {
        return Flow.of(HttpRequest.class)
                .map(request -> {
                    Map<String, String> params = request.getUri().query().toMap();
                    String url = params.get("testUrl");
                    int count = Integer.parseInt(params.get("count"));
                    return new Pair<>(url, count);
                })
                .mapAsync(4, pair -> {
                    CompletionStage<Object> result = PatternsCS.ask(storeActor, pair.first(), TIMEOUT_MILLIS);
                    return result.thenCompose(res -> {
                        if ((Long)res != StoreActor.RESPONSE_TIME_DEFAULT_VALUE) {
                            return CompletableFuture.completedFuture(res).thenApply(sum -> new Result(pair.first(), (long)sum));
                        } else {
                            Sink<Pair<String, Integer>, CompletionStage<Long>> innerSink = Flow.<Pair<String, Integer>>create()
                                    .mapConcat(p -> new ArrayList<>(Collections.nCopies(p.second(), p.first())))
                                    .mapAsync(4, p -> {
                                        Instant startTime = Instant.now();
                                        return asyncHttpClient().prepareGet(p).execute().
                                                toCompletableFuture()
                                                .thenCompose(response -> {
                                                    System.out.println(java.time.Duration.between(startTime, Instant.now()).toMillis());
                                                    return CompletableFuture.completedFuture(java.time.Duration.between(startTime, Instant.now()).toMillis());
                                                });
                                    })
                                    .toMat(Sink.fold(0L, Long::sum), Keep.right());
                            return Source.from(Collections.singletonList(pair))
                                    .toMat(innerSink, Keep.right()).run(materializer)
                                    .thenApply(sum -> new Result(pair.first(), sum/pair.second()));
                        }
                    });
                })
                .map(res -> {
                    long resultTime = res.getResponseTime();
                    System.out.println("res = " + String.valueOf(resultTime));
                    storeActor.tell(res, ActorRef.noSender());
                    return HttpResponse.create().withStatus(200).withEntity("Среднее время отклика " + String.valueOf(resultTime) + " ms");
                });
    }
}

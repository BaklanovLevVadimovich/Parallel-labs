package ru.bmstu.akka_labs;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.Route;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.util.concurrent.CompletionStage;

public class Server {

    public static void main(String[] args) throws IOException {
//        ActorSystem system = ActorSystem.create("routes");
//        final Http http = Http.get(system);
//        final ActorMaterializer materializer = ActorMaterializer.create(system);
//        Server instance = new Server();
//        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = instance.createRoute().flow(system, materializer);
//        final CompletionStage<ServerBinding> binding = http.bindAndHandle(routeFlow, ConnectHttp.toHost("localhost", 8081), materializer);
//        System.out.println("Server online at localhost:8081");
//        System.in.read();
//        binding.thenCompose(ServerBinding::unbind).thenAccept(unbound -> system.terminate());
        Object[] params = new Object[2];
        params[0] = 4;
        params[1] = 2;
        Test test = new Test("test", (float)2.0, params);
        try {
            System.out.println(runTest(new SingleTestInput(11, "var divideFn = function(a,b) { return a/b}", "divideFn", test)));
        } catch (Exception e) {}
    }
    private static String runTest(SingleTestInput input) throws ScriptException, NoSuchMethodException {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        engine.eval(input.getJsScript());
        Invocable invocable = (Invocable) engine;
        return invocable.invokeFunction(input.getFunctionName(), input.getTest().getParams()).toString();
    }

//    private Route createRoute() {
//
//    }
}

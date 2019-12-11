package ru.bmstu.parallel.lab6;

import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.Route;

import java.util.concurrent.CompletionStage;

import static akka.http.javadsl.server.Directives.*;

public class Server {

    private static final String URL_PARAMETER_NAME = "url";
    private static final String COUNT_PARAMETER_NAME = "count";

    private Http http;

    public Server(Http http) {
        this.http = http;
    }

    public Route createRoute() {
        return route(
                get(() ->
                        parameter(URL_PARAMETER_NAME,
                                url -> parameter(COUNT_PARAMETER_NAME,
                                        count -> {
                                            int countInt = Integer.parseInt(count);
                                            if (countInt > 0) {
                                                 return redirect(url, countInt);
                                            } else {
                                                return sendRequest(url);
                                            }
                                        })
                        )
                )
        );
    }

    private CompletionStage<HttpResponse> sendRequest(String url) {
        return http.singleRequest(HttpRequest.create(url));
    }

    private CompletionStage<HttpResponse> redirect(String url, int count) {

    }
}

package ru.bmstu.parallel.lab6;

import akka.http.javadsl.Http;
import akka.http.javadsl.server.Route;

public class Server {

    private Http http;

    public Server(Http http) {
        this.http = http;
    }

    public Route createRoute() {

    }
}

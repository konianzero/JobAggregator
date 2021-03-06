package org.aggregator.job.util;

import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

import org.aggregator.job.model.strategy.Strategy;

public class Util {
    public static final JSONParser JSON_PARSER = new JSONParser();

    public static Optional<Document> getDocument(String str) {
        Document doc;
        try {
            doc = Jsoup.connect(str)
                    .get();
        } catch (IOException ioe) {
            return Optional.empty();
        }
        return Optional.ofNullable(doc);
    }

    public static String getResponseString(HttpRequest request) {
        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e) {
            System.err.println(e.getMessage());
        }

        return Optional.ofNullable(response).map(HttpResponse::body).orElseThrow();
    }

    public static <T extends Strategy> T  getProxy(Strategy strategy) {
        ClassLoader classLoader = strategy.getClass().getClassLoader();
        Class<?>[] interfaces = strategy.getClass().getInterfaces();
        return (T) Proxy.newProxyInstance(classLoader, interfaces, new DynamicInvocationHandler(strategy));
    }
}

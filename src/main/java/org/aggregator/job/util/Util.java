package org.aggregator.job.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
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

@UtilityClass
@Slf4j
public class Util {
    public static final JSONParser JSON_PARSER = new JSONParser();
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public static Optional<Document> getDocument(String str) {
        Document doc;
        try {
            doc = Jsoup.connect(str).get();
        } catch (IOException ioe) {
            log.error("I/O Exception when Jsoup get document from " + str);
            return Optional.empty();
        }
        return Optional.ofNullable(doc);
    }

    public static String getResponseString(HttpRequest request) {
        HttpResponse<String> response = null;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            log.error("I/O Exception when send request to " + request.uri());
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            Thread.currentThread().interrupt();
        }

        return Optional.ofNullable(response).map(HttpResponse::body).orElseThrow();
    }

    @SuppressWarnings("unchecked")
    public static <T extends Strategy> T  getProxy(Strategy strategy) {
        ClassLoader classLoader = strategy.getClass().getClassLoader();
        Class<?>[] interfaces = strategy.getClass().getInterfaces();
        return (T) Proxy.newProxyInstance(classLoader, interfaces, new DynamicInvocationHandler(strategy));
    }
}

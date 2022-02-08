package org.aggregator.job.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.Optional;

import org.aggregator.job.model.strategy.Strategy;

@UtilityClass
@Slf4j
public class Util {

    public static Optional<Document> getDocument(String str) {
        Document doc;
        try {
            doc = Jsoup.connect(str).get();
        } catch (IOException ioe) {
            log.warn("I/O Exception when Jsoup get document from " + str);
            return Optional.empty();
        }
        return Optional.ofNullable(doc);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Strategy> T  getProxy(Strategy strategy) {
        ClassLoader classLoader = strategy.getClass().getClassLoader();
        Class<?>[] interfaces = strategy.getClass().getInterfaces();
        return (T) Proxy.newProxyInstance(classLoader, interfaces, new DynamicInvocationHandler(strategy));
    }
}

package org.aggregator.job.util;

import org.aggregator.job.model.strategy.Strategy;

import java.lang.reflect.Proxy;

public class ProxyUtil {
    public static <T extends Strategy> T  getProxy(Strategy strategy) {
        ClassLoader classLoader = strategy.getClass().getClassLoader();
        Class<?>[] interfaces = strategy.getClass().getInterfaces();
        return (T) Proxy.newProxyInstance(classLoader, interfaces, new DynamicInvocationHandler(strategy));
    }
}

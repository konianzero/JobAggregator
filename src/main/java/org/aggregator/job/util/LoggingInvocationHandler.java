package org.aggregator.job.util;

import lombok.extern.slf4j.Slf4j;
import org.aggregator.job.model.strategy.Strategy;
import org.aggregator.job.to.Vacancy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

@Slf4j(topic = "Strategies")
public class LoggingInvocationHandler implements InvocationHandler {

    private Strategy strategy;
    private String strategyClassName;

    public LoggingInvocationHandler(Strategy strategy) {
        this.strategy = strategy;
        this.strategyClassName = strategy.getClass().getSimpleName();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("Get vacancies from " + strategyClassName);
        Object methodReturns = method.invoke(strategy, args);
        log.info("{} vacancies from {}", ((List<Vacancy>) methodReturns).size(), strategyClassName);
        return methodReturns;
    }
}

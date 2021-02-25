package org.aggregator.job.util;

import org.aggregator.job.model.strategy.Strategy;
import org.aggregator.job.vo.Vacancy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

public class DynamicInvocationHandler implements InvocationHandler {
    private final Logger log;

    private Strategy strategy;

    public DynamicInvocationHandler(Strategy strategy) {
        this.strategy = strategy;
        log = LoggerFactory.getLogger(strategy.getClass());
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.debug("Get vacancies...");
        Object methodReturns = method.invoke(strategy, args);
        log.info("{} vacancies", ((List<Vacancy>) methodReturns).size());
        return methodReturns;
    }
}

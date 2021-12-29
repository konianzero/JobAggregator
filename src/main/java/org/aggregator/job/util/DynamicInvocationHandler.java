package org.aggregator.job.util;

import lombok.extern.slf4j.Slf4j;
import org.aggregator.job.model.strategy.Strategy;
import org.aggregator.job.vo.Vacancy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

@Slf4j(topic = "Strategies")
public class DynamicInvocationHandler implements InvocationHandler {

    private Strategy strategy;

    public DynamicInvocationHandler(Strategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("Get vacancies from");
        Object methodReturns = method.invoke(strategy, args);
        log.info("{} vacancies", ((List<Vacancy>) methodReturns).size());
        return methodReturns;
    }
}

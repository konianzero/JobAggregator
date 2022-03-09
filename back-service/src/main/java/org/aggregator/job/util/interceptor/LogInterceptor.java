package org.aggregator.job.util.interceptor;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import lombok.extern.slf4j.Slf4j;
import org.aggregator.job.to.Vacancy;

import java.util.List;

@Log
@Interceptor
@Slf4j(topic = "Interceptor")
public class LogInterceptor {

    @AroundInvoke
    public Object logMethod(InvocationContext ctx) throws Exception {
        String strategyClassName = ctx.getTarget().getClass().getSimpleName();
        log.info("Get vacancies from " + strategyClassName);
        Object result = ctx.proceed();
        log.info("{} vacancies from {}", ((List<Vacancy>) result).size(), strategyClassName);
        return result;
    }
}

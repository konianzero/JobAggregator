package org.aggregator.job.model;

import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.aggregator.job.model.strategy.Strategy;
import org.aggregator.job.to.Vacancy;

import java.util.List;
import java.util.concurrent.Callable;

import static org.aggregator.job.util.Util.getProxy;

@Slf4j
public class Provider implements Callable<List<Vacancy>> {
    private final Strategy strategy;
    @Setter
    private String searchParameter;

    public Provider(@NonNull Strategy strategy) {
        this.strategy = getProxy(strategy);
    }

    @Override
    public List<Vacancy> call() throws Exception {
        return strategy.getVacancies(searchParameter);
    }
}

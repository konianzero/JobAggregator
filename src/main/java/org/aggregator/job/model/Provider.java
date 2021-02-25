package org.aggregator.job.model;

import org.aggregator.job.model.strategy.Strategy;
import org.aggregator.job.vo.Vacancy;

import java.util.List;
import java.util.Objects;

public class Provider {
    private Strategy strategy;

    public void setStrategy(Strategy strategy) {
        isNull(strategy);
        this.strategy = strategy;
    }

    public Provider(Strategy strategy) {
        isNull(strategy);
        this.strategy = strategy;
    }

    public List<Vacancy> getJavaVacancies(String searchString) {
        return strategy.getVacancies(searchString);
    }

    private void isNull(Strategy strategy) {
        if (Objects.isNull(strategy)) { throw new IllegalArgumentException(); }
    }
}

package org.aggregator.job.model;

import org.aggregator.job.model.strategy.Strategy;
import org.aggregator.job.vo.Vacancy;

import java.util.List;

public class Provider {
    private Strategy strategy;

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public Provider(Strategy strategy) {
        this.strategy = strategy;
    }

    public List<Vacancy> getJavaVacancies(String searchString) {
        return strategy.getVacancies(searchString);
    }
}

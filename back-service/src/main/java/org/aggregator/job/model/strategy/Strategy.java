package org.aggregator.job.model.strategy;

import org.aggregator.job.to.Vacancy;

import java.util.List;

public interface Strategy {
    List<Vacancy> getVacancies(String searchString);
}

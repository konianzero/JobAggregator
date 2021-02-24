package org.aggregator.job.model.strategy;

import org.aggregator.job.vo.Vacancy;

import java.util.List;

public interface Strategy {
    List<Vacancy> getVacancies(String searchString);
}

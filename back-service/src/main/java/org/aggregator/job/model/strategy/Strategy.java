package org.aggregator.job.model.strategy;

import org.aggregator.job.to.Vacancy;
import org.aggregator.job.util.interceptor.Log;

import java.util.List;

public interface Strategy {
    List<Vacancy> getVacancies(String searchString);

    @Log
    default List<Vacancy> getVacanciesWithLog(String searchString) {
        return getVacancies(searchString);
    }
}

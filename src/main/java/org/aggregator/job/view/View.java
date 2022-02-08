package org.aggregator.job.view;

import org.aggregator.job.Controller;
import org.aggregator.job.to.Vacancy;

import java.util.List;

public interface View {
    void search();
    void update(List<Vacancy> vacancies);
    void setController(Controller controller);
}

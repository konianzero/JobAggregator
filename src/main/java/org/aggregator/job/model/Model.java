package org.aggregator.job.model;

import java.util.ArrayList;
import java.util.List;

import org.aggregator.job.view.View;
import org.aggregator.job.vo.Vacancy;

public class Model {
    private View view;
    private Provider[] providers;

    public Model(View view, Provider... providers) {
        if (view == null || providers == null || providers.length == 0) { throw new IllegalArgumentException(); }

        this.view = view;
        this.providers = providers;
    }

    public void selectCity(String city) {
        List<Vacancy> vacancies = new ArrayList<>();
        for (Provider provider: providers) {
            vacancies.addAll(provider.getJavaVacancies(city));
        }
        view.update(vacancies);
    }
}

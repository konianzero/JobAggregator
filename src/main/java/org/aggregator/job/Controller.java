package org.aggregator.job;

import org.aggregator.job.model.Model;

import static java.util.Objects.isNull;

public class Controller {
    private final Model model;

    public Controller(Model model) {
        if (isNull(model)) { throw new IllegalArgumentException(); }
        this.model = model;
    }

    public void onCitySelect(String cityName) {
        model.selectCity(cityName);
    }
}

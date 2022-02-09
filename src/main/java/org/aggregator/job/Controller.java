package org.aggregator.job;

import lombok.NonNull;

import org.aggregator.job.model.Model;
import org.aggregator.job.view.View;

public class Controller {
    private final View view;
    private final Model model;

    public Controller(@NonNull View view, @NonNull Model model) {
        this.view = view;
        this.model = model;
    }

    public void onSearch(String position, String location) {
        view.update(model.getVacancies(position, location));
    }
}

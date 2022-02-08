package org.aggregator.job;

import org.aggregator.job.model.Model;
import org.aggregator.job.view.View;

import static java.util.Objects.isNull;

public class Controller {
    private final View view;
    private final Model model;

    public Controller(View view, Model model) {
        if (isNull(view)) { throw new IllegalArgumentException("View is null!"); }
        if (isNull(model)) { throw new IllegalArgumentException("Model is null!"); }
        this.view = view;
        this.model = model;
    }

    public void onSearch(String searchParameter) {
        view.update(model.getVacancies(searchParameter));
    }
}

package org.aggregator.job;

import org.aggregator.job.model.Model;
import org.aggregator.job.view.View;

import static java.util.Objects.isNull;

public class Controller {
    private final View view;
    private final Model model;

    public Controller(View view, Model model) {
        if (isNull(view) || isNull(model)) { throw new IllegalArgumentException(); }
        this.view = view;
        this.model = model;
    }

    public void onSearch(String searchParameter) {
        view.update(model.setSearchParameter(searchParameter));
    }
}

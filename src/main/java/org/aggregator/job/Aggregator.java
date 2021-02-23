package org.aggregator.job;

import org.aggregator.job.model.HHStrategy;
import org.aggregator.job.model.Model;
import org.aggregator.job.model.CareerHabrStrategy;
import org.aggregator.job.model.Provider;
import org.aggregator.job.view.HtmlView;

public class Aggregator {
    public static void main(String[] args) {
        Provider[] providers = {
                new Provider(new HHStrategy()),
                new Provider(new CareerHabrStrategy())
        };
        HtmlView view = new HtmlView();
        Model model = new Model(view, providers);
        Controller controller = new Controller(model);
        view.setController(controller);

        view.userCitySelectEmulationMethod();
    }
}

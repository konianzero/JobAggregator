package org.aggregator.job;

import org.aggregator.job.model.Model;
import org.aggregator.job.model.strategy.CareerHabrStrategy;
import org.aggregator.job.model.strategy.HHStrategy;
import org.aggregator.job.model.strategy.RabotaRuStrategy;
import org.aggregator.job.model.Provider;
import org.aggregator.job.view.View;
import org.aggregator.job.view.HtmlView;

public class Aggregator {
    public static void main(String[] args) {
        Provider[] providers = {
                new Provider(new HHStrategy()),
                new Provider(new CareerHabrStrategy()),
                new Provider(new RabotaRuStrategy())
        };
        View view = new HtmlView();
        Model model = new Model(providers);
        Controller controller = new Controller(view, model);

        view.setController(controller);

        view.search();
        System.exit(0);
    }
}

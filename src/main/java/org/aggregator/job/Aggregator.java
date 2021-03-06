package org.aggregator.job;

import org.aggregator.job.model.Model;
import org.aggregator.job.model.strategy.CareerHabrStrategy;
import org.aggregator.job.model.strategy.HHStrategy;
import org.aggregator.job.model.strategy.RabotaStrategy;
import org.aggregator.job.model.Provider;
import org.aggregator.job.view.HtmlView;

import static org.aggregator.job.util.Util.getProxy;

public class Aggregator {
    public static void main(String[] args) {
        Provider[] providers = {
                new Provider(getProxy(new HHStrategy())),
                new Provider(getProxy(new CareerHabrStrategy())),
                new Provider(getProxy(new RabotaStrategy()))
        };
        HtmlView view = new HtmlView();
        Model model = new Model(view, providers);
        Controller controller = new Controller(model);

        view.setController(controller);

        view.userCitySelectEmulationMethod();
        System.exit(0);
    }
}

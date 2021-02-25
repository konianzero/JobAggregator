package org.aggregator.job;

import org.aggregator.job.model.Model;
import org.aggregator.job.model.strategy.CareerHabrStrategy;
import org.aggregator.job.model.strategy.HHStrategy;
import org.aggregator.job.model.Provider;
import org.aggregator.job.view.HtmlView;

import static org.aggregator.job.util.ProxyUtil.getProxy;

public class Aggregator {
    public static void main(String[] args) {
        Provider[] providers = {
                new Provider(getProxy(new HHStrategy())),
                new Provider(getProxy(new CareerHabrStrategy()))
        };
        HtmlView view = new HtmlView();
        Model model = new Model(view, providers);
        Controller controller = new Controller(model);

        view.setController(controller);

        view.userCitySelectEmulationMethod();
    }
}

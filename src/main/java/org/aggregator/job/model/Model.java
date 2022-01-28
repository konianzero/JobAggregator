package org.aggregator.job.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aggregator.job.view.View;
import org.aggregator.job.vo.Vacancy;

import static java.util.Objects.isNull;

@Slf4j
public class Model {
    private final Provider[] providers;
    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    public Model(Provider... providers) {
        if (isNull(providers) || providers.length == 0) { throw new IllegalArgumentException(); }

        this.providers = providers;
    }

    public List<Vacancy> selectCity(String city) {
        for (Provider provider: providers) {
            provider.setSearchString(city);
        }

        return getVacancies(providers);
    }

    @SneakyThrows
    private List<Vacancy> getVacancies(Provider[] providers) {
        List<Callable<List<Vacancy>>> callableTasks = new ArrayList<>(Arrays.asList(providers));
        List<Future<List<Vacancy>>> futures = executor.invokeAll(callableTasks);
        return futures.stream()
                .map(future -> {
                    try {
                        return future.get();
                    }
                    catch (InterruptedException | ExecutionException e) {
                        log.warn("Interrupt in stream", e);
                        throw new RuntimeException(e);
                    }
                })
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}

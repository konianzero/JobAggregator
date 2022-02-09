package org.aggregator.job.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aggregator.job.to.Vacancy;

import static java.util.Objects.isNull;

@Slf4j
public class Model {
    private final Provider[] providers;
    private final ExecutorService executor;

    public Model(Provider... providers) {
        if (isNull(providers) || providers.length == 0) { throw new IllegalArgumentException("No providers!"); }

        this.providers = providers;
        executor = Executors.newFixedThreadPool(threadsNumber());
    }

    public List<Vacancy> getVacancies(String position, String location) {
        for (Provider provider: providers) {
            provider.setSearchParameter(position + " " + location);
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
                        log.warn("Exception in stream when retrieve vacancy list", e);
                        throw new RuntimeException(e);
                    }
                })
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private int threadsNumber() {
        int jvmAvailableProcessors = Runtime.getRuntime().availableProcessors();
        int providersNum = providers.length;
        log.info("Number of providers: {}, Number of available threads: {}", providersNum, jvmAvailableProcessors);
        return providersNum > jvmAvailableProcessors ? jvmAvailableProcessors : providersNum;
    }
}

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

// TODO: https://www.javacodegeeks.com/2013/05/java-8-completablefuture-in-action.html
@Slf4j
public class Model {
    private final View view;
    private final Provider[] providers;
    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    public Model(View view, Provider... providers) {
        if (isNull(view) || isNull(providers) || providers.length == 0) { throw new IllegalArgumentException(); }

        this.view = view;
        this.providers = providers;
    }

    // TODO: https://www.baeldung.com/java-executor-service-tutorial
    //      List<Callable<List<Vacancy>>> callableTasks = new ArrayList<>()
    //      List<Future<List<Vacancy>>> futures = executorService.invokeAll(callableTasks)
    public void selectCity(String city) {
        List<Vacancy> vacancies;
        for (Provider provider: providers) {
            provider.setSearchString(city);
        }

        vacancies = getVacancies(providers);

        view.update(vacancies);
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

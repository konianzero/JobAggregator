package org.aggregator.job.model;

import java.util.*;
import java.util.concurrent.*;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.aggregator.job.model.strategy.Strategy;
import org.aggregator.job.to.Vacancy;

// TODO: https://www.javacodegeeks.com/2013/05/java-8-completablefuture-in-action.html
@ApplicationScoped
@Slf4j
public class Model {
    private Provider[] providers;
    private ExecutorService executorService;
    private CompletionService<List<Vacancy>> completionService;

    @Inject @Any
    Instance<Strategy> strategies;

    public Model() {
    }

    @PostConstruct
    public void init() {
        providers = strategies.stream()
                    .map(Provider::new)
                    .toArray(Provider[]::new);
        executorService = Executors.newFixedThreadPool(threadsNumber());
        completionService = new ExecutorCompletionService<>(executorService);
    }

    public List<Vacancy> getVacancies(String position, String location) {
        log.info("Search for parameters: {}, {}", position, location);
        for (Provider provider: providers) {
            provider.setSearchParameter(position + " " + location);
        }

        return getVacancies(providers);
    }

    @SneakyThrows
    private List<Vacancy> getVacancies(Provider[] providers) {
        List<Vacancy> allVacancies = new ArrayList<>();

        // Submit tasks
        for (Provider provider : providers) {
            completionService.submit(provider);
        }

        // Get results
        for (int i = 0; i < providers.length; i++) {
            Future<List<Vacancy>> future = completionService.take();
            List<Vacancy> vacancies = null;

            try {
                vacancies = future.get();
            } catch (InterruptedException e) {
                log.error("Interrupted while get result from task", e);
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                log.error("Exception while execute task", e);
            }

            if (Objects.nonNull(vacancies)) allVacancies.addAll(vacancies);
        }

        return allVacancies;
    }

    private int threadsNumber() {
        int jvmAvailableProcessors = Runtime.getRuntime().availableProcessors();
        int providersNum = providers.length;
        log.info("Number of providers: {}, Number of available threads: {}", providersNum, jvmAvailableProcessors);
        return providersNum > jvmAvailableProcessors ? jvmAvailableProcessors : providersNum;
    }
}

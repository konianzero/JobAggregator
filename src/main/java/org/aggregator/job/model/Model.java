package org.aggregator.job.model;

import java.util.*;
import java.util.concurrent.*;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.aggregator.job.to.Vacancy;

@Slf4j
public class Model {
    private final Provider[] providers;
    private final ExecutorService executorService;
    private final CompletionService<List<Vacancy>> completionService;

    public Model(@NonNull Provider... providers) {
        if (providers.length == 0) { throw new IllegalArgumentException("Providers is empty!"); }

        this.providers = providers;
        executorService = Executors.newFixedThreadPool(threadsNumber());
        completionService = new ExecutorCompletionService<>(executorService);
    }

    public List<Vacancy> getVacancies(String position, String location) {
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

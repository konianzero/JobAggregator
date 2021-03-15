package org.aggregator.job.model.strategy;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.aggregator.job.vo.Vacancy;

import static org.aggregator.job.util.Util.getDocument;

public class HHStrategy implements Strategy {

    private static final String URL_FORMAT = "http://hh.ru/search/vacancy?text=java+%s&page=%d";
    private static final String SITE_NAME = "Head Hunter";
    private ForkJoinPool customThreadPool;

    @Override
    public List<Vacancy> getVacancies(String searchString) {
        createForkJoinPool();
        List<Vacancy> vacancies = forkJoinSubmit(getVacancies(getElements(searchString)));
        shutDownForkJoinPool();
        return vacancies;
    }

    private void createForkJoinPool() {
        int poolSize = Runtime.getRuntime().availableProcessors();
        customThreadPool = new ForkJoinPool(poolSize);
    }

    private void shutDownForkJoinPool() {
        customThreadPool.shutdown();
        try {
            if (!customThreadPool.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
                customThreadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            customThreadPool.shutdownNow();
        }
    }

    private <T> List<T> forkJoinSubmit(Callable<List<T>> listCallable) {
        return customThreadPool.submit(listCallable).invoke();
    }

    private Callable<List<Vacancy>> getVacancies(List<Element> elementList) {
        return () -> elementList.parallelStream()
                                .map(this::getVacancy)
                                .collect(Collectors.toList());
    }

    private List<Element> getElements(String searchString) {
        List<Element> elements = new ArrayList<>();
        int pageNumber = 0;

        while (true) {
            List<Element> vacancyElements = forkJoinSubmit(getElements(getDocument(String.format(URL_FORMAT, searchString, ++pageNumber))));

            if (vacancyElements.isEmpty()) { break; }

            elements.addAll(vacancyElements);
        }
        return elements;
    }

    private Callable<List<Element>> getElements(Document document) {
        return () -> document
                .select("div[data-qa=vacancy-serp__vacancy]")
                .parallelStream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Vacancy getVacancy(Element element) {
        Vacancy vacancy = new Vacancy();

        vacancy.setTitle(element.getElementsByAttributeValueContaining("data-qa", "title").text());
        String salary = element.getElementsByAttributeValueContaining("data-qa", "compensation").text();
        vacancy.setSalary(salary == null ? "" : salary);
        vacancy.setCity(element.getElementsByAttributeValueContaining("data-qa", "address").text());
        vacancy.setCompanyName(element.getElementsByAttributeValue("data-qa", "vacancy-serp__vacancy-employer").text());
        vacancy.setSiteName(SITE_NAME);
        vacancy.setUrl(element.getElementsByAttributeValueContaining("data-qa", "title").attr("href"));

        return vacancy;
    }
}

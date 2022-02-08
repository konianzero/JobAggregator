package org.aggregator.job.model.strategy;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.aggregator.job.to.Vacancy;

import static org.aggregator.job.util.Util.getDocument;

public class HHStrategy implements Strategy {

    private static final String URL_FORMAT = "http://hh.ru/search/vacancy?text=java+developer+%s&page=%d";
    private static final String SITE_NAME = "Head Hunter";

    @Override
    public List<Vacancy> getVacancies(String searchString) {
        return vacanciesFrom(processPage(searchString));
    }

    private List<Vacancy> vacanciesFrom(List<Element> vacancyElements) {
        return vacancyElements.stream()
                          .map(this::mapToVacancy)
                          .collect(Collectors.toList());
    }

    private List<Element> processPage(String searchString) {
        return Stream.iterate(0, i -> i + 1)
                     .map(pageNumber -> getDocument(String.format(URL_FORMAT, searchString, pageNumber)))
                     .takeWhile(Optional::isPresent)
                     .map(Optional::get)
                     .map(this::getVacanciesElements)
                     .flatMap(Collection::stream)
                     .collect(Collectors.toList());
    }

    private List<Element> getVacanciesElements(Document document) {
        return document.select("div[data-qa^='vacancy-serp__vacancy ']")
                       .stream()
                       .filter(Objects::nonNull)
                       .collect(Collectors.toList());
    }

    private Vacancy mapToVacancy(Element element) {
        Vacancy vacancy = new Vacancy();

        vacancy.setTitle(element.select("a[data-qa=vacancy-serp__vacancy-title]").text());
        vacancy.setSalary(Optional.ofNullable(element.select("span[data-qa=vacancy-serp__vacancy-compensation]").text()).orElse(""));
        vacancy.setCity(element.select("div[data-qa=vacancy-serp__vacancy-address]").text());
        vacancy.setCompanyName(element.select("a[data-qa=vacancy-serp__vacancy-employer]").text());
        vacancy.setSiteName(SITE_NAME);
        vacancy.setUrl(element.select("a[data-qa=vacancy-serp__vacancy-title]").attr("href"));

        return vacancy;
    }
}

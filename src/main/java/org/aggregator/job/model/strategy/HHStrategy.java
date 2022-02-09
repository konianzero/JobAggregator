package org.aggregator.job.model.strategy;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.aggregator.job.to.Vacancy;
import org.jsoup.select.Elements;

import static org.aggregator.job.util.Util.getDocument;

public class HHStrategy implements Strategy {

    private static final String URL = "https://hh.ru";
    private static final String VACANCIES = URL + "/search/vacancy?text=%s&page=%d";
    private static final String SITE_NAME = "Head Hunter";

    @Override
    public List<Vacancy> getVacancies(String searchString) {
        return vacanciesFrom(processPage(searchString));
    }

    private List<Vacancy> vacanciesFrom(List<Element> vacanciesElements) {
        return vacanciesElements.stream()
                          .map(this::mapToVacancy)
                          .collect(Collectors.toList());
    }

    private List<Element> processPage(String searchString) {
        return Stream.iterate(0, i -> i + 1)
                     .map(pageNumber -> getDocument(String.format(VACANCIES, searchString, pageNumber)))
                     .takeWhile(Optional::isPresent)
                     .map(Optional::get)
                     .map(this::getVacanciesElements)
                     .flatMap(Collection::stream)
                     .collect(Collectors.toList());
    }

    private Elements getVacanciesElements(Document document) {
        return document.select("div[data-qa^='vacancy-serp__vacancy ']");
    }

    private Vacancy mapToVacancy(Element element) {
        Optional<String> workFromHome = Optional.of(element.select("div[data-qa=vacancy-serp__vacancy-work-schedule]").text());
        return Vacancy.builder()
                      .title(element.select("a[data-qa=vacancy-serp__vacancy-title]").text())
                      .salary(Optional.of(element.select("span[data-qa=vacancy-serp__vacancy-compensation]").text()).orElse(""))
                      .location(element.select("div[data-qa=vacancy-serp__vacancy-address]").text() + workFromHome.map(s -> ", " + s).orElse(""))
                      .companyName(element.select("a[data-qa=vacancy-serp__vacancy-employer]").text())
                      .siteName(SITE_NAME)
                      .link(element.select("a[data-qa=vacancy-serp__vacancy-title]").attr("href"))
                      .build();
    }
}

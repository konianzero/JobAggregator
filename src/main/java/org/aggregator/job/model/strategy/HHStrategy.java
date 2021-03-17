package org.aggregator.job.model.strategy;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.aggregator.job.vo.Vacancy;

import static org.aggregator.job.util.Util.getDocument;

public class HHStrategy implements Strategy {

    private static final String URL_FORMAT = "http://hh.ru/search/vacancy?text=java+%s&page=%d";
    private static final String SITE_NAME = "Head Hunter";

    @Override
    public List<Vacancy> getVacancies(String searchString) {
        return getVacancies(getElements(searchString));
    }

    private List<Vacancy> getVacancies(List<Element> elementList) {
        return elementList.stream()
                          .map(this::getVacancy)
                          .collect(Collectors.toList());
    }

    private List<Element> getElements(String searchString) {
        return Stream.iterate(0, i -> i + 1)
                     .map(pageNumber -> getDocument(String.format(URL_FORMAT, searchString, ++pageNumber)))
                     .takeWhile(Optional::isPresent)
                     .map(Optional::get)
                     .map(this::getElements)
                     .flatMap(Collection::stream)
                     .collect(Collectors.toList());
    }

    private List<Element> getElements(Document document) {
        return document.select("div[data-qa=vacancy-serp__vacancy]")
                       .stream()
                       .filter(Objects::nonNull)
                       .collect(Collectors.toList());
    }

    private Vacancy getVacancy(Element element) {
        Vacancy vacancy = new Vacancy();

        vacancy.setTitle(element.select("a[data-qa$=vacancy-title]").text());
        vacancy.setSalary(Optional.ofNullable(element.select("span[data-qa$=vacancy-compensation]").text()).orElse(""));
        vacancy.setCity(element.select("span[data-qa$=vacancy-address]").text());
        vacancy.setCompanyName(element.select("a[data-qa$=vacancy-employer]").text());
        vacancy.setSiteName(SITE_NAME);
        vacancy.setUrl(element.select("a[data-qa$=vacancy-title]").attr("href"));

        return vacancy;
    }
}

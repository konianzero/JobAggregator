package org.aggregator.job.model.strategy;

import org.aggregator.job.to.Vacancy;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;
import static org.aggregator.job.util.Util.getDocument;

public class SuperJobStrategy implements Strategy {

    private static final String URL = "https://www.superjob.ru";
    private static final String VACANCIES = URL + "/vacancy/search/?keywords=%s&page=%d";
    private static final String SITE_NAME = "SuperJob";

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
        return Stream.iterate(1, i -> i + 1)
                .map(pageNumber -> getDocument(String.format(VACANCIES, searchString, pageNumber)))
                .takeWhile(Optional::isPresent)
                .map(Optional::get)
                .map(this::getVacanciesElements)
                .takeWhile(not(ArrayList::isEmpty))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private Elements getVacanciesElements(Document document) {
        return document.select("div[class$=f-test-vacancy-item _1fma_ _2nteL]");
    }

    private Vacancy mapToVacancy(Element element) {
        return Vacancy.builder()
                .title(element.select("a[class^=icMQ_ _6AfZ9]").text())
                .salary(Optional.of(element.select("span[class$=f-test-text-company-item-salary]").text()).orElse(""))
                .location(element.select("span[class^=f-test-text-company-item-location]").text().replace(" • ", ", "))
                .companyName(element.select("span[class*=f-test-text-vacancy-item-company-name]").text())
                .siteName(SITE_NAME)
                .link(URL + element.select("a[class^=icMQ_ _6AfZ9]").attr("href"))
                .build();
    }
}

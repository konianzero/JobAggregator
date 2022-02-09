package org.aggregator.job.model.strategy;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.aggregator.job.to.Vacancy;

import static java.util.function.Predicate.not;
import static org.aggregator.job.util.Util.*;

public class RabotaRuStrategy implements Strategy {

    private static final String URL = "https://spb.rabota.ru";
    private static final String VACANCIES = URL + "/vacancy/?query=java+developer+%s&page=%d";
    private static final String SITE_NAME = "Работа.ру";

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
                .takeWhile(not(e -> e.nodeName().equals("div") && e.className().equals("r-serp-similar-title r-serp__item") && e.child(0).text().equals("Похожие вакансии")))
                .filter(e -> e.nodeName().equals("article"))
                .collect(Collectors.toList());
    }

    private Elements getVacanciesElements(Document document) {
        return document.select("div[class=infinity-scroll r-serp__infinity-list]").first().children();
    }

    private Vacancy mapToVacancy(Element element) {
        return Vacancy.builder()
                      .title(element.getElementsByClass("vacancy-preview-card__title").text())
                      .salary(Optional.of(element.getElementsByClass("vacancy-preview-card__salary").text()).orElse(""))
                      .location(element.getElementsByClass("vacancy-preview-location__address-text").text())
                      .companyName(element.getElementsByClass("vacancy-preview-card__company-name").text())
                      .siteName(SITE_NAME)
                      .link(URL + element.getElementsByClass("vacancy-preview-card__title").first().child(0).attr("href"))
                      .build();
    }
}

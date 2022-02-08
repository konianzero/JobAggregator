package org.aggregator.job.model.strategy;

import lombok.extern.slf4j.Slf4j;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.aggregator.job.to.Vacancy;

import static java.util.function.Predicate.not;
import static org.aggregator.job.util.Util.*;

@Slf4j
public class RabotaRuStrategy implements Strategy {

    private static final String URL = "https://www.rabota.ru/vacancy/?query=java+developer+%s&page=%d";
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
                .map(pageNumber -> getDocument(String.format(URL, searchString, pageNumber)))
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
        Vacancy vacancy = new Vacancy();

        vacancy.setTitle(element.getElementsByClass("vacancy-preview-card__title").text());
        vacancy.setSalary(Optional.ofNullable(element.getElementsByClass("vacancy-preview-card__salary").text()).orElse(""));
        vacancy.setCity(element.getElementsByClass("vacancy-preview-location__address-text").text());
        vacancy.setCompanyName(element.getElementsByClass("vacancy-preview-card__company-name").text());
        vacancy.setSiteName(SITE_NAME);
        vacancy.setUrl("https://spb.rabota.ru" + element.getElementsByClass("vacancy-preview-card__title").first().child(0).attr("href"));

        return vacancy;
    }
}

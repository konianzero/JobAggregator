package org.aggregator.job.model.strategy;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import org.aggregator.job.vo.Vacancy;

import static org.aggregator.job.util.Util.getDocument;

public class HHStrategy implements Strategy {

    private static final String URL_FORMAT = "http://hh.ru/search/vacancy?text=java+%s&page=%d";
    private static final String SITE_NAME = "Head Hunter";

    @Override
    public List<Vacancy> getVacancies(String searchString) {
        List<Vacancy> vacancies = new ArrayList<>();

        for (Element element : getElements(searchString)) {
            if (element == null) { continue; }

            vacancies.add(getVacancy(element));
        }

        return vacancies;
    }

    private List<Element> getElements(String searchString) {
        List<Element> elements = new ArrayList<>();
        Document doc;
        int pageNumber = 0;

        while (true) {
            doc = getDocument(String.format(URL_FORMAT, searchString, ++pageNumber));
            Elements vacancyElements = doc.getElementsByAttributeValue("data-qa", "vacancy-serp__vacancy");

            if (vacancyElements.size() == 0) { break; }

            elements.addAll(vacancyElements);
        }

        return elements;
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

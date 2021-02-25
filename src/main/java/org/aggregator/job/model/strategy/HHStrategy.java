package org.aggregator.job.model.strategy;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.aggregator.job.vo.Vacancy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HHStrategy implements Strategy {
    private static final Logger log = LoggerFactory.getLogger(HHStrategy.class);

    private static final String URL_FORMAT = "http://hh.ru/search/vacancy?text=java+%s&page=%d";
    private static final String SITE_NAME = "Head Hunter";

    @Override
    public List<Vacancy> getVacancies(String searchString) {
        log.debug("Get vacancies...");
        List<Vacancy> vacancies = new ArrayList<>();
        Document doc;
        int pageNumber = 0;
        while (true) {
            doc = getDocument(searchString, ++pageNumber);
            Elements vacancyElements = doc.getElementsByAttributeValue("data-qa", "vacancy-serp__vacancy");
            if (vacancyElements.size() == 0) { break; }

            for (Element element : vacancyElements) {
                if (element == null) { continue; }

                Vacancy vacancy = new Vacancy();
                vacancy.setTitle(element.getElementsByAttributeValueContaining("data-qa", "title").text());
                String salary = element.getElementsByAttributeValueContaining("data-qa", "compensation").text();
                vacancy.setSalary(salary == null ? "" : salary);
                vacancy.setCity(element.getElementsByAttributeValueContaining("data-qa", "address").text());
                vacancy.setCompanyName(element.getElementsByAttributeValue("data-qa", "vacancy-serp__vacancy-employer").text());
                vacancy.setSiteName(SITE_NAME);
                vacancy.setUrl(element.getElementsByAttributeValueContaining("data-qa", "title").attr("href"));

                vacancies.add(vacancy);
            }
        }
        log.info("{} vacancies", vacancies.size());
        return vacancies;
    }

    private Document getDocument(String searchString, int page) {
        Document doc = null;
        try {
            doc = Jsoup.connect(String.format(URL_FORMAT, searchString, page))
                       .get();
        } catch (IOException ioe) { ioe.printStackTrace(); }
        return doc;
    }
}

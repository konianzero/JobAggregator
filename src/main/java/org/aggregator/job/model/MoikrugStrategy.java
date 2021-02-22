package org.aggregator.job.model;

import org.aggregator.job.vo.Vacancy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MoikrugStrategy implements Strategy {
    private final static String URL_FORMAT = "https://moikrug.ru/vacancies?q=java+%s&page=%d";

    @Override
    public List<Vacancy> getVacancies(String searchString) {
        List<Vacancy> vacancies = new ArrayList<>();
        Document doc;
        int pageNumber = 0;
        try {
            while (true) {
                doc = getDocument(searchString, pageNumber++);
                Elements vacancyElements = doc.getElementsByClass("Job");
                if (vacancyElements.size() == 0) { break; }

                for (Element element : vacancyElements) {
                    if (element == null) { continue; }

                    Vacancy vacancy = new Vacancy();
                    vacancy.setTitle(element.getElementsByClass("title").text());
                    String salary = element.getElementsByClass("salary").text();
                    vacancy.setSalary(salary == null ? "" : salary);
                    vacancy.setCity(element.getElementsByClass("location").text());
                    vacancy.setCompanyName(element.getElementsByClass("company_name").text());
                    vacancy.setSiteName(URL_FORMAT);
                    vacancy.setUrl("https://moikrug.ru" + element.getElementsByClass("title").first().child(0).attr("href"));

                    vacancies.add(vacancy);
                }
            }
        } catch (IOException io) { io.printStackTrace(); }
        return vacancies;
    }

    protected Document getDocument(String searchString, int page) throws IOException {
        Document doc = null;
        try{
            doc = Jsoup.connect(String.format(URL_FORMAT, searchString, page))
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.")
                    .referrer("")
                    .get();
        } catch (IOException ioe) { ioe.printStackTrace(); }
        return doc;
    }
}

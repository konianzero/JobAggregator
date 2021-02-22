package org.aggregator.job.model;

//import com.google.common.io.Files;
import org.aggregator.job.vo.Vacancy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HHStrategy implements Strategy {
    private final static String URL_FORMAT = "http://hh.ru/search/vacancy?text=java+%s&page=%d";

    @Override
    public List<Vacancy> getVacancies(String searchString) {
        List<Vacancy> vacancies = new ArrayList<>();
        Document doc;
        int pageNumber = 0;
        try {
            doc = getDocument(searchString, pageNumber);
            while (true) {
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
                    vacancy.setSiteName(URL_FORMAT);
                    vacancy.setUrl(element.getElementsByAttributeValueContaining("data-qa", "title").attr("href"));

                    vacancies.add(vacancy);
                }
                doc = getDocument(searchString, ++pageNumber);
            }
        } catch (IOException io) { io.printStackTrace(); }
        return vacancies;
    }

    protected Document getDocument(String searchString, int page) throws IOException {
        Document doc = null;
        try{
            doc = Jsoup.connect(String.format(URL_FORMAT, searchString, page))
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.125 Safari/537.36")
                    .referrer("")
                    .get();
//            String shtml = doc.html();
//            Files.write(shtml.getBytes(),
//            Paths.get("C:\SoftwareProjects\Java\IdeaProjects\JavaRushTasks\4.JavaCollections\src\shtml.html").toFile());
//            System.out.println(shtml);
        } catch (IOException ioe) { ioe.printStackTrace(); }
        return doc;
    }
}

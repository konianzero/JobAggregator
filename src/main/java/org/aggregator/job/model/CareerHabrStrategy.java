package org.aggregator.job.model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.aggregator.job.vo.Vacancy;

public class CareerHabrStrategy implements Strategy {
    private final static String URL_FORMAT = "https://career.habr.com/vacancies?city_id=%s&page=%d&q=java&type=all";

    @Override
    public List<Vacancy> getVacancies(String searchString) {
        List<Vacancy> vacancies = new ArrayList<>();
        Document doc;
        int pageNumber = 0;
        String cityId = getCityId(searchString);
        while (true) {
            doc = getDocument(cityId, ++pageNumber);
            Elements vacancyElements = doc.getElementsByClass("vacancy-card");
            if (vacancyElements.size() == 0) { break; }

            for (Element element : vacancyElements) {
                if (element == null) { continue; }

                Vacancy vacancy = new Vacancy();
                vacancy.setTitle(element.getElementsByClass("vacancy-card__title").text());
                String salary = element.getElementsByClass("basic-salary").text();
                vacancy.setSalary(salary == null ? "" : salary);
                vacancy.setCity(element.getElementsByClass("vacancy-card__meta").first().child(0).text());
                vacancy.setCompanyName(element.getElementsByClass("vacancy-card__company-title").text());
                vacancy.setSiteName(URL_FORMAT);
                vacancy.setUrl("https://career.habr.com" + element.getElementsByClass("vacancy-card__title").first().child(0).attr("href"));

                vacancies.add(vacancy);
            }
        }
        return vacancies;
    }

    private Document getDocument(String cityId, int page) {
        Document doc = null;
        try {
            doc = Jsoup.connect(String.format(URL_FORMAT, cityId, page))
                       .get();
        } catch (IOException ioe) { ioe.printStackTrace(); }
        return doc;
    }

    private String getCityId(String searchString) {
        String cityId = null;
        try {
            cityId = parseJsonResponse(
                Jsoup.connect(String.format("https://career.habr.com/api/frontend/suggestions/locations?term=%s", searchString))
                        .ignoreContentType(true).get()
                        .body().text()
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityId;
    }

    private String parseJsonResponse(String text) {
        String res = null;
        JSONParser jsonParser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(text);
            JSONArray lang = (JSONArray) jsonObject.get("list");
            res = ((JSONObject) lang.get(0)).get("value").toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return res;
    }
}

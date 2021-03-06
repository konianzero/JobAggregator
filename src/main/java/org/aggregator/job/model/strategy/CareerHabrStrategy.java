package org.aggregator.job.model.strategy;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.aggregator.job.vo.Vacancy;

import static org.aggregator.job.util.Util.*;

public class CareerHabrStrategy implements Strategy {

    private static final String URL_FORMAT = "https://career.habr.com/vacancies?city_id=%s&page=%d&q=java&type=all";
    private static final String SITE_NAME = "Хабр Карьера";

    @Override
    public List<Vacancy> getVacancies(String searchString) {
        return getVacancies(getElements(searchString));
    }

    private List<Vacancy> getVacancies(List<Element> elementList) {
        List<Vacancy> vacancies = new ArrayList<>();

        for (Element element : elementList) {
            if (element == null) { continue; }

            vacancies.add(getVacancy(element));
        }

        return vacancies;
    }

    private List<Element> getElements(String searchString) {
        List<Element> elements = new ArrayList<>();
        int pageNumber = 0;
        String cityId = getCityId(searchString);

        if (Objects.isNull(cityId)) { return elements; }

        while (true) {
            Optional<Elements> vacancyElements = getDocument(String.format(URL_FORMAT, cityId, ++pageNumber))
                    .map(doc -> doc.getElementsByClass("vacancy-card"));

            if (vacancyElements.isEmpty() || vacancyElements.get().size() == 0) { break; }

            elements.addAll(vacancyElements.get());
        }

        return elements;
    }

    private Vacancy getVacancy(Element element) {
        Vacancy vacancy = new Vacancy();

        vacancy.setTitle(element.getElementsByClass("vacancy-card__title").text());
        String salary = element.getElementsByClass("basic-salary").text();
        vacancy.setSalary(salary == null ? "" : salary);
        vacancy.setCity(element.getElementsByClass("vacancy-card__meta").first().child(0).text());
        vacancy.setCompanyName(element.getElementsByClass("vacancy-card__company-title").text());
        vacancy.setSiteName(SITE_NAME);
        vacancy.setUrl("https://career.habr.com" + element.getElementsByClass("vacancy-card__title").first().child(0).attr("href"));

        return vacancy;
    }

    private String getCityId(String searchString) {
        return parseJsonResponse(getResponseString(makeRequest(searchString)));
    }

    private String parseJsonResponse(String text) {
        String res = null;
        try {
            JSONObject jsonObject = (JSONObject) JSON_PARSER.parse(text);
            JSONArray list = (JSONArray) jsonObject.get("list");
            res = ((JSONObject) list.get(0)).get("value").toString();
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return res;
    }

    private HttpRequest makeRequest(String searchString) {
        URI locationUri = URI.create(String.format("https://career.habr.com/api/frontend/suggestions/locations?term=%s", searchString));
        return HttpRequest.newBuilder()
                          .uri(locationUri)
                          .GET()
                          .build();
    }
}

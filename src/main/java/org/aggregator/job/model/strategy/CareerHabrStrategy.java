package org.aggregator.job.model.strategy;

import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.aggregator.job.to.Vacancy;

import static java.util.function.Predicate.not;
import static org.aggregator.job.util.Util.getDocument;

@Slf4j
public class CareerHabrStrategy implements Strategy {

    private static final String URL = "https://career.habr.com/vacancies?city_id=%s&page=%d&q=java&type=all";
    private static final String SITE_NAME = "Хабр Карьера";

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
        List<Element> elements = new ArrayList<>();
        String cityId = getCityId(searchString);

        if (Objects.isNull(cityId)) { return elements; }

        return Stream.iterate(0, i -> i + 1)
                .map(pageNumber -> getDocument(String.format(URL, cityId, pageNumber)))
                .takeWhile(Optional::isPresent)
                .map(Optional::get)
                .map(this::getVacanciesElements)
                .takeWhile(not(ArrayList::isEmpty))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private Elements getVacanciesElements(Document document) {
        return document.getElementsByClass("vacancy-card");
    }

    private Vacancy mapToVacancy(Element element) {
        Vacancy vacancy = new Vacancy();

        vacancy.setTitle(element.getElementsByClass("vacancy-card__title").text());
        String salary = element.getElementsByClass("basic-salary").text();
        vacancy.setSalary(salary == null ? "" : salary);
        vacancy.setCity(element.getElementsByClass("vacancy-card__meta").text().replace(" · ", ", "));
        vacancy.setCompanyName(element.getElementsByClass("vacancy-card__company-title").text());
        vacancy.setSiteName(SITE_NAME);
        vacancy.setUrl("https://career.habr.com" + element.getElementsByClass("vacancy-card__title").first().child(0).attr("href"));

        return vacancy;
    }


    /* *************************************
     * *** Helper methods to get city ID ***
     * *************************************/

    public static final JSONParser JSON_PARSER = new JSONParser();
    private static final HttpClient httpClient = HttpClient.newHttpClient();

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

    public static String getResponseString(HttpRequest request) {
        HttpResponse<String> response = null;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            log.error("I/O Exception when send request to " + request.uri());
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            Thread.currentThread().interrupt();
        }

        return Optional.ofNullable(response).map(HttpResponse::body).orElseThrow();
    }

    private HttpRequest makeRequest(String searchString) {
        return HttpRequest.newBuilder()
                          .uri(URI.create("https://career.habr.com/api/frontend/suggestions/locations?term=" + searchString))
                          .GET()
                          .build();
    }
}

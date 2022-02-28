package org.aggregator.job.model.strategy;

import jakarta.enterprise.context.ApplicationScoped;
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

@ApplicationScoped
@Slf4j
public class CareerHabrStrategy implements Strategy {

    private static final String URL = "https://career.habr.com";
    private static final String VACANCIES = URL + "/vacancies?city_id=%s&page=%d&q=%s&type=all";
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
        String[] searchStr = searchString.split("(?! [\\w]) ");
        String cityId = getCityId(searchStr[1]);

        if (Objects.isNull(cityId)) { return elements; }

        return Stream.iterate(0, i -> i + 1)
                .map(pageNumber -> getDocument(String.format(VACANCIES, cityId, pageNumber, searchStr[0])))
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
        return Vacancy.builder()
                      .title(element.getElementsByClass("vacancy-card__title").text())
                      .salary(Optional.of(element.getElementsByClass("basic-salary").text()).orElse(""))
                      .location(element.getElementsByClass("vacancy-card__meta").text().replace(" · ", ", "))
                      .companyName(element.getElementsByClass("vacancy-card__company-title").text())
                      .siteName(SITE_NAME)
                      .link(URL + element.getElementsByClass("vacancy-card__title").first().child(0).attr("href"))
                      .build();
    }


    /* ************************************* *
     * *** Helper methods to get city ID *** *
     * ************************************* */

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
            log.error("Exception when parse JSON\n:  " + text, pe);
        }
        return res;
    }

    public static String getResponseString(HttpRequest request) {
        HttpResponse<String> response = null;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException ioe) {
            log.error("I/O Exception when send request to " + request.uri(), ioe);
        } catch (InterruptedException ie) {
            log.error("Interrupt when send request to " + request.uri(), ie);
            Thread.currentThread().interrupt();
        }

        return Optional.ofNullable(response).map(HttpResponse::body).orElse("");
    }

    private HttpRequest makeRequest(String searchString) {
        return HttpRequest.newBuilder()
                          .uri(URI.create("https://career.habr.com/api/frontend/suggestions/locations?term=" + searchString))
                          .GET()
                          .build();
    }
}

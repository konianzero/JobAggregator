package org.aggregator.job.model.strategy;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.*;
import java.net.http.HttpRequest;
import java.util.*;

import org.aggregator.job.vo.Vacancy;

import static org.aggregator.job.util.Util.*;

public class RabotaStrategy implements Strategy {

    private static final String URL_FORMAT = "https://www.rabota.ru/vacancy/?query=java&location.regionId=%s&location.name=%s&page=%d";
    private static final String SITE_NAME = "Работа.ру";

    @Override
    public List<Vacancy> getVacancies(String searchString) {
        List<Vacancy> vacancies = new ArrayList<>();
        Document doc;
        int pageNumber = 0;
        boolean hasVacancies = true;
        String cityId = getCityId(searchString);
        if (Objects.isNull(cityId)) { return List.of(); }

        while (hasVacancies) {
            doc = getDocument(String.format(URL_FORMAT, cityId, searchString, ++pageNumber));
            Elements vacancyElements = doc.getElementsByClass("infinity-scroll r-serp__infinity-list").first().children();

            for (Element element : vacancyElements) {
                if (element.attr("class").equals("r-serp-similar-title r-serp__item")) {
                    hasVacancies = false;
                    break;
                }

                if (!element.nodeName().equals("article")) {
                    continue;
                }

                Vacancy vacancy = new Vacancy();
                vacancy.setTitle(element.getElementsByClass("vacancy-preview-card__title").text());
                String salary = element.getElementsByClass("vacancy-preview-card__salary").text();
                vacancy.setSalary(salary == null ? "" : salary);
                vacancy.setCity(element.getElementsByClass("vacancy-preview-location__address-text").text());
                vacancy.setCompanyName(element.getElementsByClass("vacancy-preview-card__company-name").text());
                vacancy.setSiteName(SITE_NAME);
                vacancy.setUrl("https://spb.rabota.ru" + element.getElementsByClass("vacancy-preview-card__title").first().child(0).attr("href"));

                vacancies.add(vacancy);
            }
        }
        return vacancies;
    }

    private String getCityId(String searchString) {
        return parseJsonResponse(getResponseString(makeRequest(searchString)), searchString);
    }

    private String parseJsonResponse(String text, String searchString) {
        String res = null;
        try {
            JSONObject jsonObject = (JSONObject) JSON_PARSER.parse(text);
            JSONObject response = (JSONObject) jsonObject.get("response");
            JSONArray regions = (JSONArray) response.get("regions");
            for (Object o : regions) {
                JSONObject region = (JSONObject) o;
                if (region.get("name").toString().equals(searchString)) {
                    res = region.get("id").toString();
                    break;
                }
            }
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return res;
    }

    private HttpRequest makeRequest(String searchString) {
        String data = String.format("""
                {
                  "request": {
                    "filter": {
                      "query": "%s"
                    }
                  }
                }
                """, searchString);
        return HttpRequest.newBuilder()
                          .uri(URI.create("https://api.rabota.ru/v4/regions/suggest.json"))
                          .header("Content-Type", "application/json")
                          .POST(HttpRequest.BodyPublishers.ofString(data))
                          .build();
    }
}

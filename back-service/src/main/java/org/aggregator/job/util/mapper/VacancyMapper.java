package org.aggregator.job.util.mapper;

import jakarta.json.*;
import lombok.experimental.UtilityClass;
import org.aggregator.job.to.Vacancy;

import java.util.List;

@UtilityClass
public class VacancyMapper {
    public static JsonArray map(List<Vacancy> vacancies) {
        final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        vacancies.forEach(vacancy -> {
            JsonObject jsonObject = map(vacancy);
            arrayBuilder.add(jsonObject);
        });
        return arrayBuilder.build();
    }

    public static JsonObject map(Vacancy vacancy) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        addValue(builder, "title", vacancy.getTitle());
        addValue(builder, "salary", vacancy.getSalary());
        addValue(builder, "location", vacancy.getLocation());
        addValue(builder, "companyName", vacancy.getCompanyName());
        addValue(builder, "siteName", vacancy.getSiteName());
        addValue(builder, "link", vacancy.getLink());
        return builder.build();
    }

    private static void addValue(JsonObjectBuilder builder, String key, Object value) {
        if (value != null) {
            builder.add(key, value.toString());
        } else {
            builder.addNull(key);
        }
    }
}

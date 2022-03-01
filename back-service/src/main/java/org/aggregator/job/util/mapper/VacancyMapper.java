package org.aggregator.job.util.mapper;

import jakarta.json.*;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.aggregator.job.to.Vacancy;

import java.util.List;

@UtilityClass
@Slf4j
public class VacancyMapper {

    public static String map(List<Vacancy> vacancies) {
        String jsonArray = null;
        log.info("Map vacancies list to JSON");
        try (Jsonb jsonb = JsonbBuilder.create()) {
            jsonArray = jsonb.toJson(vacancies);
        } catch (Exception e) {
            log.error("Exception while mapping Vacancies to Json Array", e);
        }
        return jsonArray;
    }
}

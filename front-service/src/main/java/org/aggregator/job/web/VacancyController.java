package org.aggregator.job.web;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.JsonArray;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import lombok.extern.slf4j.Slf4j;
import org.aggregator.job.back.BackService;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@RequestScoped
@Path("/vacancies")
@Slf4j
public class VacancyController {

    @Inject
    @RestClient
    private BackService backService;

    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonArray getVacancies(@QueryParam("position") String position, @QueryParam("location") String location) {
        return backService.onSearch(position, location);
    }
}

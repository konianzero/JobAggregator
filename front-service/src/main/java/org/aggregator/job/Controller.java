package org.aggregator.job;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import lombok.extern.slf4j.Slf4j;
import org.aggregator.job.back.BackService;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@RequestScoped
@Path("/vacancies")
@Slf4j
public class Controller {

    @Inject
    @RestClient
    private BackService backService;

    @GET
    @Path("")
    public String getVacancies(@QueryParam("position") String position, @QueryParam("location") String location) {
        return backService.onSearch(position, location);
    }
}

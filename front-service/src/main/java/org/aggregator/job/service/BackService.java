package org.aggregator.job.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.aggregator.job.to.Vacancy;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@Path("/vacancies")
@RegisterRestClient
public interface BackService {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<Vacancy> onSearch(@QueryParam("position") String position, @QueryParam("location") String location);
}

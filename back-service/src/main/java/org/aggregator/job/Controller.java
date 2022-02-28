package org.aggregator.job;

import java.util.List;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.NonNull;

import lombok.extern.slf4j.Slf4j;
import org.aggregator.job.model.Model;
import org.aggregator.job.to.Vacancy;

@RequestScoped
@Path("/vacancies")
@Slf4j
public class Controller {

    @Inject
    private Model model;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Vacancy> onSearch(@QueryParam("position") String position, @QueryParam("location") String location) {
        return model.getVacancies(position, location);
    }
}

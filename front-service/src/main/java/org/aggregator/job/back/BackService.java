package org.aggregator.job.back;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient
@ApplicationScoped
public interface BackService {

    @GET
    String onSearch(@QueryParam("position") String position, @QueryParam("location") String location);
}

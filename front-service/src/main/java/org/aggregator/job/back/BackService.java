package org.aggregator.job.back;

import javax.json.JsonArray;
import javax.ws.rs.GET;;
import javax.ws.rs.QueryParam;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient
public interface BackService {

    @GET
    JsonArray onSearch(@QueryParam("position") String position, @QueryParam("location") String location);
}

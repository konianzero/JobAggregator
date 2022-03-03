package org.aggregator.job.config;

import lombok.extern.slf4j.Slf4j;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import java.io.InputStream;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static java.lang.String.format;

@Path("")
@RequestScoped
@Slf4j
public class StaticResources {

    @Inject
    ServletContext context;

    /**
     * Serving static files form folders:
     *
     * /WEB-INF/resources
     * /WEB-INF/static
     * /WEB-INF/public
     * /WEB-INF/assets
     */
    @GET
    //@Path("{path: ^(assets|public|static|resources)\\/.*}")
    //@Path("{path: ^assets\\/.*}")
    @Path("{path:.*}")
    public Response staticResources(@PathParam("path") final String path) {
        log.debug("Handling static resources: {}", path);
        InputStream resource = context.getResourceAsStream(format("/WEB-INF/%s", path));
        return null == resource
                ? Response.status(NOT_FOUND).build()
                : Response.ok().entity(resource).build();
    }
}

package org.aggregator.job.web;

import lombok.extern.slf4j.Slf4j;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.mvc.View;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.aggregator.job.service.BackService;
import org.aggregator.job.to.Vacancy;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.stream.Collectors;

// https://platform.sh/blog/2019/microservices-in-the-cloud-part-two/
@Controller
@Path("/vacancies")
@Slf4j
public class VacancyController {

    @Inject
    private Models models;

    @Inject
    @RestClient
    private BackService backService;

    @GET
    //@Path("")
    //@View("vacancies.html")
    @Produces(MediaType.TEXT_HTML)
    public String getVacancies(@QueryParam("position") String position, @QueryParam("location") String location) {
        // TODO - Interceptor for {http://web.job.aggregator.org/}VacancyController has thrown exception, unwinding now javax.ws.rs.core.Configuration context class has not been injected. Check if ContextProvider supporting this class is registered
        //models.put("vacancies", backService.onSearch(position, location));
        //List<Vacancy> vacancies = backService.onSearch(position, location);
        List<Vacancy> vacancies = getList();
        log.info("Vacancies: \n{}", vacancies.stream().map(Vacancy::toString).collect(Collectors.joining(",\n")));
        models.put("vacancies", vacancies);
        return "vacancies";
    }

    private List<Vacancy> getList() {
        return List.of(
                Vacancy.builder().title("Test1").salary("1$").location("T1").companyName("Com1").siteName("site1").link("link1").build(),
                Vacancy.builder().title("Test2").salary("2$").location("T2").companyName("Com2").siteName("site2").link("link2").build(),
                Vacancy.builder().title("Test3").salary("3$").location("T3").companyName("Com3").siteName("site3").link("link3").build(),
                Vacancy.builder().title("Test4").salary("4$").location("T4").companyName("Com4").siteName("site4").link("link4").build(),
                Vacancy.builder().title("Test5").salary("5$").location("T5").companyName("Com5").siteName("site5").link("link5").build()
        );
    }
}

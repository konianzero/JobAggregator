package org.aggregator.job.config;

import io.vavr.control.Try;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.mvc.engine.ViewEngineContext;
import javax.mvc.engine.ViewEngineException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.krazo.engine.ViewEngineBase;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

@ApplicationScoped
@Slf4j
public class ThymeleafViewEngine extends ViewEngineBase {

    @Inject
    ServletContext servletContext;

    @Inject
    TemplateEngine templateEngine;

    @Override
    public boolean supports(String view) {
        return !view.contains(".");
    }

    @Override
    public void processView(ViewEngineContext context) throws ViewEngineException {
        log.debug("Configure ViewEngine...");

        HttpServletRequest req = context.getRequest(HttpServletRequest.class);
        HttpServletResponse res = context.getResponse(HttpServletResponse.class);

        Try.of(() -> new WebContext(req, res, servletContext, req.getLocale()))
                .andThenTry(webContext -> webContext.setVariables(context.getModels().asMap()))
                .andThenTry(() -> req.setAttribute("view", context.getView()))
                .andThenTry(webContext ->
                        templateEngine.process(
                                // /*default layout*/ "default",
                                context.getView(),
                                webContext,
                                res.getWriter()
                        )
                )
                .getOrElseThrow(ViewEngineException::new);
    }
}

package org.aggregator.job.config;

import lombok.extern.slf4j.Slf4j;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import javax.servlet.ServletContext;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

@ApplicationScoped
@Slf4j
public class ThymeleafProducer {

    @Produces
    @Singleton
    ServletContextTemplateResolver servletContextTemplateResolver(ServletContext servletContext) {
        log.debug("Producing ServletContext template resolver...");
        var resolver = new ServletContextTemplateResolver(servletContext);
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".html");
        resolver.setCacheable(false);
        return resolver;
    }

    @Produces
    @Singleton
    TemplateEngine templateEngine(ServletContextTemplateResolver resolver) {
        log.debug("Producing Thymeleaf template engine...");
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(resolver);
        return templateEngine;
    }
}

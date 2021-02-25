package org.aggregator.job.util;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.StringWriter;
import java.util.List;

import org.aggregator.job.vo.Vacancy;

public class HtmlTemplateUtil {
    public static String processHMTLTemplate(String templateName, List<Vacancy> vacancies) {
        TemplateEngine templateEngine = new TemplateEngine();
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();

        resolver.setPrefix("/templates/");
        resolver.setSuffix(".html");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setTemplateMode(TemplateMode.HTML);
        templateEngine.setTemplateResolver(resolver);

        Context context = new Context();
        context.setVariable("vacancies", vacancies);

        StringWriter stringWriter = new StringWriter();
        templateEngine.process(templateName, context, stringWriter);
        return stringWriter.toString();
    }
}

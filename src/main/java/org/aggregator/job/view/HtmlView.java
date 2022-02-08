package org.aggregator.job.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.aggregator.job.Controller;
import org.aggregator.job.to.Vacancy;
import org.aggregator.job.util.HtmlTemplateUtil;

import static java.util.Objects.isNull;

public class HtmlView implements View {
    private static final Logger log = LoggerFactory.getLogger(HtmlView.class);

    private final Path filePath = Path.of("vacancies.html");
    private Controller controller;

    @Override
    public void setController(Controller controller) {
        if (isNull(controller)) { throw new IllegalArgumentException("Controller is null!"); }
        this.controller = controller;
    }

    @Override
    public void update(List<Vacancy> vacancies) {
        updateFile(createHTML(vacancies));
    }

    private void updateFile(String str) {
        try {
            createFileIfNotExists();
            Files.writeString(filePath, str);
            log.info("File created {}", filePath.toAbsolutePath());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void createFileIfNotExists() throws IOException {
        if (Files.notExists(filePath)) {
            Files.createFile(filePath);
        }
    }

    private String createHTML(List<Vacancy> vacancies) {
        log.info("Generating table of {} vacancies", vacancies.size());
        return HtmlTemplateUtil.processHMTLTemplate("vacanciesTemplate", vacancies);
    }

    public void search() {
        controller.onSearch("Санкт-Петербург");
    }
}

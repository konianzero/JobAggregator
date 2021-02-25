package org.aggregator.job.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.aggregator.job.Controller;
import org.aggregator.job.vo.Vacancy;
import org.aggregator.job.util.HtmlTemplateUtil;

import static java.util.Objects.isNull;

public class HtmlView implements View {
    private static final Logger log = LoggerFactory.getLogger(HtmlView.class);

    private final String filePath = "vacancies.html";
    private Controller controller;

    @Override
    public void setController(Controller controller) {
        if (isNull(controller)) { throw new IllegalArgumentException(); }
        this.controller = controller;
    }

    @Override
    public void update(List<Vacancy> vacancies) {
        updateFile(createHTML(vacancies));
        log.info("Table of vacancies is generated");
    }

    private void updateFile(String str) {
        try {
            createFileIfNotExists();
            Files.writeString(Path.of(filePath), str);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void createFileIfNotExists() throws IOException {
        if (Files.notExists(Path.of(filePath))) {
            Files.createFile(Path.of(filePath));
        }
    }

    private String createHTML(List<Vacancy> vacancies) {
        return HtmlTemplateUtil.processHMTLTemplate("vacanciesTemplate", vacancies);
    }

    public void userCitySelectEmulationMethod() {
        controller.onCitySelect("Санкт-Петербург");
    }
}

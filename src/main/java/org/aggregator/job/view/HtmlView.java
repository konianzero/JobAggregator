package org.aggregator.job.view;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.aggregator.job.Controller;
import org.aggregator.job.to.Vacancy;
import org.aggregator.job.util.HtmlTemplateUtil;

@Slf4j
public class HtmlView implements View {
    private final Path filePath = Path.of("vacancies.html");
    private Controller controller;

    @Override
    public void setController(@NonNull Controller controller) {
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
        controller.onSearch("Java developer", "Санкт-Петербург");
    }
}

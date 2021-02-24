package org.aggregator.job.view;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.aggregator.job.Controller;
import org.aggregator.job.vo.Vacancy;
import org.aggregator.job.Util.HtmlTemplateUtil;

public class HtmlView implements View {
    private final String filePath = "src/main/resources/vacancies.html";
    private Controller controller;

    @Override
    public void setController(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void update(List<Vacancy> vacancies) {
        try {
            updateFile(createHTML(vacancies));
        } catch (Exception e) { e.printStackTrace(); }
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

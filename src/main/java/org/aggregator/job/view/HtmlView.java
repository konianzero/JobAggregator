package org.aggregator.job.view;

import org.aggregator.job.Controller;
import org.aggregator.job.vo.Vacancy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class HtmlView implements View {
    private final String filePath = "./4.JavaCollections/src/" + this.getClass().getPackage().getName().replace('.', '/') + "/vacancies.html";
    private Controller controller;

    @Override
    public void setController(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void update(List<Vacancy> vacancies) {
        try {
            updateFile(getUpdatedFileContent(vacancies));
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void updateFile(String str) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(str);
        } catch (IOException ioe) { ioe.printStackTrace(); }
    }

    private String getUpdatedFileContent(List<Vacancy> vacancies) {
        Document document = null;
        Element templateCopy = null;
        try {
            document = getDocument();
            // Получи элемент, у которого есть класс template
            Element template = document.getElementsByClass("template").first();
            // Сделай копию этого объекта
            templateCopy = template.clone();
            // удали из нее атрибут "style" и класс "template"
            templateCopy.removeAttr("style");
            templateCopy.removeClass("template");
            // Удали все добавленные ранее вакансии. У них единственный класс "vacancy"
            // Но тег tr, у которого class="vacancy template", не удаляй.
            document.select("tr[class=vacancy]").remove().not("tr[class=vacancy template]");
//            document.select("tr.vacancy").remove().not("tr.vacancy template");
            for (Vacancy vacancy : vacancies) {
                Element localClone = templateCopy.clone();// клонируем шаблон тега
                localClone.getElementsByClass("city").first().text(vacancy.getCity());
                localClone.getElementsByClass("companyName").first().text(vacancy.getCompanyName());
                localClone.getElementsByClass("salary").first().text(vacancy.getSalary());

                Element link = localClone.getElementsByTag("a").first();
                link.text(vacancy.getTitle());
                link.attr("href", vacancy.getUrl());

                template.before(localClone.outerHtml());
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
            return "Some exception occurred";
        }
        return document.html();
    }

    protected Document getDocument() throws IOException {
        return Jsoup.parse(Paths.get(filePath).toFile(), "UTF-8");
    }

    public void userCitySelectEmulationMethod() {
        controller.onCitySelect("Санкт-Петербург");
    }
}

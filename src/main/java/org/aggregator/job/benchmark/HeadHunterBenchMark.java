package org.aggregator.job.benchmark;

import org.jsoup.nodes.Element;

import org.openjdk.jmh.annotations.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.aggregator.job.model.strategy.HHStrategy;

import static org.aggregator.job.util.Util.getDocument;

@State(Scope.Benchmark)
@Fork(value = 1, warmups = 0)
@Warmup(iterations = 0)
@BenchmarkMode(Mode.AverageTime)
public class HeadHunterBenchMark {

    private String searchString = "Санкт-Петербург";
    private int pageNumber = 1;
    private String urlFormat;

    private List<Element> elementList;
    private Method getElements;
    private Method getVacancies;
    private HHStrategy hhStrategy;

    @Setup
    public void setup() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        hhStrategy = new HHStrategy();

        Field field = HHStrategy.class.getDeclaredField("URL_FORMAT");
        field.setAccessible(true);
        urlFormat = (String) field.get(hhStrategy);

        getElements = HHStrategy.class.getDeclaredMethod("getElements", String.class);
        getVacancies = HHStrategy.class.getDeclaredMethod("getVacancies", List.class);

        getElements.setAccessible(true);
        getVacancies.setAccessible(true);

        elementList = (List<Element>) getElements.invoke(hhStrategy, searchString);
    }

    @Benchmark
    public void hhGetElements() throws InvocationTargetException, IllegalAccessException {
        getElements.invoke(hhStrategy, searchString);
    }

    @Benchmark
    public void hhGetDocument() {
        getDocument(String.format(urlFormat, searchString, pageNumber));
    }

    @Benchmark
    public void hhGetVacancies() throws InvocationTargetException, IllegalAccessException {
        getVacancies.invoke(hhStrategy, elementList);
    }
}

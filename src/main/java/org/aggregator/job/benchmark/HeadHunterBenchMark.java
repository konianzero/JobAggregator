package org.aggregator.job.benchmark;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.openjdk.jmh.annotations.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Callable;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import org.aggregator.job.model.strategy.HHStrategy;

import static org.aggregator.job.util.Util.getDocument;

@State(Scope.Benchmark)
@Fork(value = 1, warmups = 0)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@BenchmarkMode(Mode.AverageTime)
public class HeadHunterBenchMark {

    private String searchString = "Санкт-Петербург";
    private int pageNumber = 1;
    private String urlFormat;

    private Document document;
    private List<Element> elementList;
    private Method forkJoinSubmit;
    private Method getElementsString;
    private Method getElements;
    private Method getVacancies;
    private HHStrategy hhStrategy;

    @Setup
    public void setup() {
        hhStrategy = new HHStrategy();

        try {
            Field field = HHStrategy.class.getDeclaredField("URL_FORMAT");
            field.setAccessible(true);
            urlFormat = (String) field.get(hhStrategy);

            Method createPool = HHStrategy.class.getDeclaredMethod("createForkJoinPool");
            createPool.setAccessible(true);
            createPool.invoke(hhStrategy);

            getElementsString = HHStrategy.class.getDeclaredMethod("getElements", String.class);
            forkJoinSubmit = HHStrategy.class.getDeclaredMethod("forkJoinSubmit", Callable.class);
            getElements = HHStrategy.class.getDeclaredMethod("getElements", Document.class);
            getVacancies = HHStrategy.class.getDeclaredMethod("getVacancies", List.class);
        } catch (NoSuchMethodException | NoSuchFieldException | IllegalAccessException | InvocationTargetException e) {
            System.err.println(e.getMessage());
        }

        getElementsString.setAccessible(true);
        forkJoinSubmit.setAccessible(true);
        getElements.setAccessible(true);
        getVacancies.setAccessible(true);

        document = getDocument(String.format(urlFormat, searchString, pageNumber));
        try {
            elementList = (List<Element>) getElementsString.invoke(hhStrategy, searchString);
        } catch (IllegalAccessException | InvocationTargetException e) {
            System.err.println(e.getMessage());
        }
    }

    @TearDown
    public void cleanup() {
        try {
            Method closePool = HHStrategy.class.getDeclaredMethod("shutDownForkJoinPool");
            closePool.setAccessible(true);
            closePool.invoke(hhStrategy);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            System.err.println(e.getMessage());
        }

        searchString = null;
        urlFormat = null;

        document = null;
        elementList = null;
        forkJoinSubmit = null;
        getElements = null;
        getVacancies = null;
        hhStrategy = null;
    }

    @Benchmark
    public void hhGetElements() {
        try {
            forkJoinSubmit.invoke(hhStrategy, getElements.invoke(hhStrategy, document));
        } catch (IllegalAccessException | InvocationTargetException e) {
            System.err.println(e.getMessage());
        }
    }

    @Benchmark
    public void hhGetElementsString() {
        try {
            getElementsString.invoke(hhStrategy, searchString);
        } catch (IllegalAccessException | InvocationTargetException e) {
            System.err.println(e.getMessage());
        }
    }

    @Benchmark
    @Warmup(iterations = 0)
    public void hhGetDocument() {
        getDocument(String.format(urlFormat, searchString, pageNumber));
    }

    @Benchmark
    public void hhGetVacancies() {
        try {
            forkJoinSubmit.invoke(hhStrategy, getVacancies.invoke(hhStrategy, elementList));
        } catch (IllegalAccessException | InvocationTargetException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void main(String[] args) throws RunnerException {
        new Runner(
                new OptionsBuilder()
                        .include(HeadHunterBenchMark.class.getSimpleName())
                        .build()
        ).run();
    }
}

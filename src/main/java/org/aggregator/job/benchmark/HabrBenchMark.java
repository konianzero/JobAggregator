package org.aggregator.job.benchmark;

import org.jsoup.nodes.Element;

import org.openjdk.jmh.annotations.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.aggregator.job.model.strategy.CareerHabrStrategy;

import static org.aggregator.job.util.Util.getDocument;

@State(Scope.Benchmark)
@Fork(value = 1, warmups = 0)
@Warmup(iterations = 0)
@BenchmarkMode(Mode.AverageTime)
public class HabrBenchMark {

    private String searchString = "Санкт-Петербург";
    private int pageNumber = 1;
    private String urlFormat;
    private String cityId;

    private List<Element> elementList;
    private Method getElements;
    private Method getVacancies;
    private Method getCityId;
    private CareerHabrStrategy habrStrategy;

    @Setup
    public void setup() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        habrStrategy = new CareerHabrStrategy();

        Field field = CareerHabrStrategy.class.getDeclaredField("URL_FORMAT");
        field.setAccessible(true);
        urlFormat = (String) field.get(habrStrategy);

        getElements = CareerHabrStrategy.class.getDeclaredMethod("getElements", String.class);
        getVacancies = CareerHabrStrategy.class.getDeclaredMethod("getVacancies", List.class);
        getCityId = CareerHabrStrategy.class.getDeclaredMethod("getCityId", String.class);

        getElements.setAccessible(true);
        getVacancies.setAccessible(true);
        getCityId.setAccessible(true);

        elementList = (List<Element>) getElements.invoke(habrStrategy, searchString);
        cityId = (String) getCityId.invoke(habrStrategy, searchString);
    }

    @Benchmark
    public void habrGetElements() throws InvocationTargetException, IllegalAccessException {
        getElements.invoke(habrStrategy, searchString);
    }

    @Benchmark
    public void habrGetDocument() {
        getDocument(String.format(urlFormat, cityId, pageNumber));
    }

    @Benchmark
    public void habrGetCityId() throws InvocationTargetException, IllegalAccessException {
        getCityId.invoke(habrStrategy, searchString);
    }

    @Benchmark
    public void habrGetVacancies() throws InvocationTargetException, IllegalAccessException {
        getVacancies.invoke(habrStrategy, elementList);
    }
}

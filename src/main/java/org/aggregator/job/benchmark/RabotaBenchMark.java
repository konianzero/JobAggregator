package org.aggregator.job.benchmark;

import org.jsoup.nodes.Element;

import org.openjdk.jmh.annotations.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.aggregator.job.model.strategy.RabotaStrategy;

import static org.aggregator.job.util.Util.getDocument;

@State(Scope.Benchmark)
@Fork(value = 1, warmups = 0)
@Warmup(iterations = 0)
@BenchmarkMode(Mode.AverageTime)
public class RabotaBenchMark {

    private String searchString = "Санкт-Петербург";
    private int pageNumber = 1;
    private String urlFormat;
    private String cityId;

    private List<Element> elementList;
    private Method getElements;
    private Method getVacancies;
    private Method getCityId;
    private RabotaStrategy rabotaStrategy;

    @Setup
    public void setup() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        rabotaStrategy = new RabotaStrategy();

        Field field = RabotaStrategy.class.getDeclaredField("URL_FORMAT");
        field.setAccessible(true);
        urlFormat = (String) field.get(rabotaStrategy);

        getElements = RabotaStrategy.class.getDeclaredMethod("getElements", String.class);
        getVacancies = RabotaStrategy.class.getDeclaredMethod("getVacancies", List.class);
        getCityId = RabotaStrategy.class.getDeclaredMethod("getCityId", String.class);

        getElements.setAccessible(true);
        getVacancies.setAccessible(true);
        getCityId.setAccessible(true);

        elementList = (List<Element>) getElements.invoke(rabotaStrategy, searchString);
        cityId = (String) getCityId.invoke(rabotaStrategy, searchString);
    }

    @Benchmark
    public void rabotaGetElements() throws InvocationTargetException, IllegalAccessException {
        getElements.invoke(rabotaStrategy, searchString);
    }

    @Benchmark
    public void rabotaGetDocument() {
        getDocument(String.format(urlFormat, cityId, searchString, pageNumber));
    }

    @Benchmark
    public void rabotaGetCityId() throws InvocationTargetException, IllegalAccessException {
        getCityId.invoke(rabotaStrategy, searchString);
    }

    @Benchmark
    public void rabotaGetVacancies() throws InvocationTargetException, IllegalAccessException {
        getVacancies.invoke(rabotaStrategy, elementList);
    }
}

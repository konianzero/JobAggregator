package org.aggregator.job.benchmark;

import org.openjdk.jmh.annotations.*;

import org.aggregator.job.model.Provider;
import org.aggregator.job.model.strategy.CareerHabrStrategy;
import org.aggregator.job.model.strategy.HHStrategy;
import org.aggregator.job.model.strategy.RabotaStrategy;

import static org.aggregator.job.util.Util.getProxy;

@State(Scope.Benchmark)
@Fork(value = 1, warmups = 0)
@Warmup(iterations = 0)
@BenchmarkMode(Mode.AverageTime)
public class BenchMark {

    private Provider[] providers;
    private String searchString = "Санкт-Петербург";

    @Setup
    public void setup() {
        providers = new Provider[] {
                new Provider(getProxy(new HHStrategy())),
                new Provider(getProxy(new CareerHabrStrategy())),
                new Provider(getProxy(new RabotaStrategy()))
        };
    }

    @Benchmark
    public void HeadHunter() {
        providers[0].getJavaVacancies(searchString);
    }

    @Benchmark
    public void HabrCareer() {
        providers[1].getJavaVacancies(searchString);
    }

    @Benchmark
    public void Rabota() {
        providers[2].getJavaVacancies(searchString);
    }
}

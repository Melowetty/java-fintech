package ru.melowetty.rabbit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import ru.melowetty.Shared;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@BenchmarkMode({Mode.Throughput, Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
@Warmup(iterations = 1)
@Measurement(iterations = 3)
@State(Scope.Thread)
public class RabbitBenchmark {
    public static final String RABBIT_HOST = "localhost";
    public static final String QUEUE_NAME = "test";

    private List<RabbitProducer> producers;
    private List<RabbitConsumer> consumers;

    @Param({"simple", "load_balancing", "multiple_consumers", "load_balancing_multiple_consumers", "stress_test"})
    private String configuration;


    @Setup(Level.Trial)
    public void setup()  {
        try {
            int producerCount = Shared.getProducersByConfiguration(configuration);
            int consumerCount = Shared.getConsumersByConfiguration(configuration);

            producers = new ArrayList<>();
            consumers = new ArrayList<>();

            for (int i = 0; i < producerCount; i++) {
                producers.add(new RabbitProducer());
            }

            for (int i = 0; i < consumerCount; i++) {
                consumers.add(new RabbitConsumer());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    public void rabbitBench(Blackhole blackhole) {
        producers.forEach(producer -> {
            try {
                String message = Shared.MESSAGE;
                producer.sendMessage(message);
                blackhole.consume(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            blackhole.consume(producer);
        });

        consumers.forEach(consumer -> {
            try {
                String message = consumer.getMessage();
                blackhole.consume(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            blackhole.consume(consumer);
        });

        blackhole.consume(producers);
        blackhole.consume(consumers);
    }

    @TearDown(Level.Trial)
    public void teardown() {
        producers.forEach(RabbitProducer::close);
        consumers.forEach(RabbitConsumer::close);
    }

}

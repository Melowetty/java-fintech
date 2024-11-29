package ru.melowetty.elkdemo.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DemoService {
    private final Counter metric;

    public DemoService(MeterRegistry meterRegistry) {
        metric = meterRegistry.counter("demo_service_requests", "name", "custom_metric");
    }

    public void process() {
        try {
            log.info("Processing...");
            metric.increment();
            Thread.sleep(1000);
            log.info("Processed!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stackOverflowDemo() {
        stackOverflowDemo();
    }

    public void outOfMemoryDemo() {
        var arr = new int[Integer.MAX_VALUE];
    }
}

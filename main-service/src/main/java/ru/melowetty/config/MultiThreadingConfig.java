package ru.melowetty.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.testcontainers.shaded.com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

@Component
public class MultiThreadingConfig {
    @Value("${api.kudago.max-threads}")
    private int kudagoMaxActiveThreadsCount;

    @Value("${api.kudago.initialize-threads}")
    private int initializeThreads;

    @Bean
    @Qualifier("kudago_semaphore")
    public Semaphore kudagoSemaphore() {
        return new Semaphore(kudagoMaxActiveThreadsCount);
    }

    @Bean
    @Qualifier("init_command_threads")
    public ExecutorService initCommandExecutors() {
        return Executors.newFixedThreadPool(
                initializeThreads,
                new ThreadFactoryBuilder().setNameFormat("init-data-thread-%d").build());
    }
}

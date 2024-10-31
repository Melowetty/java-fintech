package ru.melowetty.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.testcontainers.shaded.com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;

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

    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor() {
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("async-executor-");
        executor.initialize();
        return executor;
    }

    @Bean
    @Qualifier("init_command_threads")
    public ExecutorService initCommandExecutors() {
        return Executors.newFixedThreadPool(
                initializeThreads,
                new ThreadFactoryBuilder().setNameFormat("init-data-%d").build());
    }

    @Bean
    @Qualifier("scheduled_init_command_threads")
    public ScheduledExecutorService scheduledInitDataExecutors() {
        return Executors.newScheduledThreadPool(
                3,
                new ThreadFactoryBuilder().setNameFormat("scheduled-init-data-%d").build()
        );
    }
}

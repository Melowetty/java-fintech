package ru.melowetty.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.Semaphore;

@Component
public class MultiThreadingConfig {
    @Value("${api.kudago.max-threads}")
    private int kudagoMaxActiveThreadsCount;

    @Bean
    @Qualifier("kudago_semaphore")
    public Semaphore kudagoSemaphore() {
        return new Semaphore(kudagoMaxActiveThreadsCount);
    }
}

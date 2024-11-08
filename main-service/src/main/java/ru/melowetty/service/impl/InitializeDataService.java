package ru.melowetty.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.melowetty.annotation.Timed;
import ru.melowetty.command.InitCommand;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class InitializeDataService {
    private final List<InitCommand> commands;
    private final ExecutorService executorService;
    private final ScheduledExecutorService scheduledExecutorService;

    @Value("${api.kudago.init-data-rate}")
    private Duration initDataRate;

    public InitializeDataService(List<InitCommand> commands,
                                 @Qualifier("init_command_threads")
                                 ExecutorService executorService,
                                 @Qualifier("scheduled_init_command_threads")
                                 ScheduledExecutorService scheduledExecutorService) {
        this.commands = commands;
        this.executorService = executorService;
        this.scheduledExecutorService = scheduledExecutorService;
    }

    @EventListener(ApplicationStartedEvent.class)
    public void scheduleInitData() {
        scheduledExecutorService.scheduleAtFixedRate(this::initData, 0,
                initDataRate.toMillis(), TimeUnit.MILLISECONDS);

    }

    @Timed
    private void initData() {
        try {
            log.info("Запущен процесс инициализации данных");
            var tasks = new ArrayList<Callable<Object>>();
            commands.forEach((command) -> {
                tasks.add(Executors.callable(command::execute));
            });
            executorService.invokeAll(tasks);
            log.info("Данные инициализированы");
        } catch (InterruptedException e) {
            log.error("Произошла ошибка во время инициализации данных", e);
        }
    }
}

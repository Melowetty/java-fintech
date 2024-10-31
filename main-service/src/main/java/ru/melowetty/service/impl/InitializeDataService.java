package ru.melowetty.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.melowetty.annotation.Timed;
import ru.melowetty.command.InitCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
@Slf4j
public class InitializeDataService {
    private final List<InitCommand> commands;
    private final ExecutorService executorService;

    public InitializeDataService(List<InitCommand> commands,
                                 @Qualifier("init_command_threads")
                                 ExecutorService executorService) {
        this.commands = commands;
        this.executorService = executorService;
    }

    @EventListener(ApplicationStartedEvent.class)
    @Timed
    public void initData() {
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

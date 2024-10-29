package ru.melowetty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.melowetty.event.EventType;
import ru.melowetty.event.impl.CategoryEventManager;
import ru.melowetty.event.impl.LocationEventManager;
import ru.melowetty.repository.impl.CategoryRepositoryImpl;
import ru.melowetty.repository.impl.LocationRepositoryImpl;
import ru.melowetty.service.impl.CategoryTransactionService;
import ru.melowetty.service.impl.LocationTransactionService;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        var context = SpringApplication.run(Main.class, args);

        var locationEventManager = context.getBean(LocationEventManager.class);
        var locationEventListener = context.getBean(LocationRepositoryImpl.class);
        var locationTransactionService = context.getBean(LocationTransactionService.class);
        locationEventManager.subscribe(EventType.CREATED, locationEventListener);

        var categoryEventManager = context.getBean(CategoryEventManager.class);
        var categoryEventListener = context.getBean(CategoryRepositoryImpl.class);
        var categoryTransactionService = context.getBean(CategoryTransactionService.class);
        categoryEventManager.subscribe(EventType.CREATED, categoryEventListener);

        for (var eventType : EventType.values()) {
            locationEventManager.subscribe(eventType, locationTransactionService);
            categoryEventManager.subscribe(eventType, categoryTransactionService);
        }
    }
}
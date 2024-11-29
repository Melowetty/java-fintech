package ru.melowetty.elkdemo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.melowetty.elkdemo.service.DemoService;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/demo")
public class DemoController {
    private final DemoService demoService;

    @GetMapping
    public void demo() {
        var uuid = UUID.randomUUID().toString();
        try(var mdc = MDC.putCloseable("requestId", uuid)) {
            log.info("Start processing");
            demoService.process();
            log.info("Task completed!");
        }
    }
}

package ru.melowetty.config;

import org.springframework.context.annotation.Bean;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RestTemplateConfiguration {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RetryTemplate retryTemplate() {
        var template = new RetryTemplate();

        var retryPolicy = new SimpleRetryPolicy();
        template.setRetryPolicy(retryPolicy);

        var backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(500);
        template.setBackOffPolicy(backOffPolicy);

        return template;
    }
}

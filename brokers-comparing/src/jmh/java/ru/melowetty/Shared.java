package ru.melowetty;

import java.util.HashMap;

public class Shared {
    private static final HashMap<String, Integer> producersByConfiguration = new HashMap<>();
    private static final HashMap<String, Integer> consumersByConfiguration = new HashMap<>();

    public static final String MESSAGE = "Тестовое сообщение";

    static {
        producersByConfiguration.put("simple", 1);
        consumersByConfiguration.put("simple", 1);

        producersByConfiguration.put("load_balancing", 3);
        consumersByConfiguration.put("load_balancing", 1);

        producersByConfiguration.put("multiple_consumers", 1);
        consumersByConfiguration.put("multiple_consumers", 3);

        producersByConfiguration.put("load_balancing_multiple_consumers", 3);
        consumersByConfiguration.put("load_balancing_multiple_consumers", 3);

        producersByConfiguration.put("stress_test", 10);
        consumersByConfiguration.put("stress_test", 10);
    }

    private Shared() {}

    public static int getConsumersByConfiguration(String configuration) {
        return consumersByConfiguration.get(configuration);
    }

    public static int getProducersByConfiguration(String configuration) {
        return producersByConfiguration.get(configuration);
    }
}

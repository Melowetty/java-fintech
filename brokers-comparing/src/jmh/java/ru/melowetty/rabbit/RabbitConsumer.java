package ru.melowetty.rabbit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static ru.melowetty.rabbit.RabbitBenchmark.QUEUE_NAME;
import static ru.melowetty.rabbit.RabbitBenchmark.RABBIT_HOST;

public class RabbitConsumer {
    private final Connection connection;
    private final Channel channel;

    public RabbitConsumer() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(RABBIT_HOST);
            this.connection = factory.newConnection();
            this.channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getMessage() throws IOException {
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
        };
        return channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
        });
    }

    public void close() {
        try {
            if (channel != null) {
                channel.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

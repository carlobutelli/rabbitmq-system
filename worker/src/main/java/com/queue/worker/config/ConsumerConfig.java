package com.queue.worker.config;

import java.util.Arrays;
import java.util.List;

public class ConsumerConfig {
    public final String serverHost;
    public final int serverPort;
    public final String serverUser;
    public final String serverPassword;
    public final String vHost;
    public final String queueName;
    public final String exchangeName;
    public final List<String> topics;
    public final int concurrency;
    public final int maxServerWaitTime;

    public ConsumerConfig(String serverHost,
                          int serverPort,
                          String serverUser,
                          String serverPassword,
                          String vHost,
                          String queueName,
                          String exchangeName,
                          List<String> topics,
                          int concurrency,
                          int maxServerWaitTime) {
        this.serverPort = serverPort;
        this.serverHost = serverHost;
        this.serverUser = serverUser;
        this.serverPassword = serverPassword;
        this.vHost = vHost;
        this.queueName = queueName;
        this.exchangeName = exchangeName;
        this.topics = topics;
        this.concurrency = concurrency;
        this.maxServerWaitTime = maxServerWaitTime;
    }

    public static ConsumerConfig fromEnvironment() {
        String bindingList = System.getenv().get("RABBITMQ_BINDING_LIST");

        List<String> bindings = Arrays.asList(bindingList.split(","));

        return new ConsumerConfig(
                System.getenv("RABBITMQ_HOST"),
                Integer.parseInt(System.getenv("RABBITMQ_PORT")),
                System.getenv("RABBITMQ_USER"),
                System.getenv("RABBITMQ_PASSWORD"),
                System.getenv("RABBITMQ_VHOST"),
                System.getenv("RABBITMQ_QUEUE"),
                System.getenv("RABBITMQ_EXCHANGE"),
                bindings,
                Integer.parseInt(System.getenv("RABBITMQ_CONCURRENCY")),
                Integer.parseInt(System.getenv("RABBITMQ_MAX_SERVER_WAIT_TIME"))
        );
    }
}

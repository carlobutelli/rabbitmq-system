package com.queue.worker.client;

import com.queue.worker.config.ConsumerConfig;
import com.queue.worker.handler.HandlerConsumer;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

public class Consumer {

    private static Logger log = LoggerFactory.getLogger(Consumer.class);
    private final ConsumerConfig config;

    public Consumer(ConsumerConfig consumerConfig) {
        this.config = consumerConfig;
    }

    public void start() throws Exception {
        Connection connection = createConnection();
        startConsumingMessages(connection);
    }

    private void startConsumingMessages(final Connection connection) throws IOException {
        List<Channel> channels = new ArrayList<>();

        for(int i=0; i < config.concurrency; i++) {
            log.info(String.format("[*] creating channel %s...", i));
            Channel channel = connection.createChannel();
            channel.basicQos(1);
            channels.add(channel);
            log.info("[*] creating exchange and queue if necessary ...");
            channel.queueDeclare(this.config.queueName, false, false, false, null);

//            for (String topic : this.config.topics) {
//                channel.queueBind(this.config.queueName, this.config.exchangeName, topic);
//            }
        }

        for(int i = 0; i < this.config.concurrency; i++) {
            log.info(String.format("[*] creating consumer %s...", i));
            try {
                Channel channel = channels.get(i);
                channel.basicConsume(
                        this.config.queueName,
                        false,
                        String.format("rabbit_consumer_%s", i),
                        new HandlerConsumer(channel)
                );
            } catch (Exception e) {
                log.error("[*] unexpected exception while consuming...", e);
                throw new IOException(e);
            }
        }
    }

    private Connection createConnection() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(this.config.serverUser);
        factory.setPassword(this.config.serverPassword);
        factory.setVirtualHost(this.config.vHost);
        factory.setHost(this.config.serverHost);
        factory.setAutomaticRecoveryEnabled(true);
        factory.setNetworkRecoveryInterval(10000);

        Connection rabbitConn = null;

        long startTime = System.currentTimeMillis();
        long giveUpTime = startTime + config.maxServerWaitTime * 1000L;

        while(rabbitConn == null && System.currentTimeMillis() < giveUpTime) {
            try {
                rabbitConn = factory.newConnection(Executors.newFixedThreadPool(this.config.concurrency));
            }
            catch (Exception e) {
                log.debug("[*] waiting for server ...");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e1) {
                    throw new RuntimeException(e1);
                }
            }
        }

        return factory.newConnection(Executors.newFixedThreadPool(config.concurrency));
    }

}


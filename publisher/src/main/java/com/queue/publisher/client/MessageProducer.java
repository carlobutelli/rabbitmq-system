package com.queue.publisher.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.queue.publisher.api.messages.MessageBody;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class MessageProducer {

    private static final Logger log = LoggerFactory.getLogger(MessageProducer.class);

    private String QUEUE_NAME = System.getenv("RABBITMQ_QUEUE");
    private final String EXCHANGE_NAME = System.getenv("RABBITMQ_EXCHANGE");
    private List<String> bindings = Arrays.asList(System.getenv("RABBITMQ_BINDING_LIST").split(","));
    private final String ROUTING_KEY_SUCCESS = this.bindings.get(0);
    private final String ROUTING_KEY_FAILURE = this.bindings.get(1);

    public MessageProducer() { }

    private String getRoutingKey(boolean success) {
        if (success)
            return this.ROUTING_KEY_SUCCESS;
        return this.ROUTING_KEY_FAILURE;
    }

    public void putMessageIntoQueue(String proccessId, String payload, boolean success) throws IOException {
        if (payload.isEmpty()) {
            log.info("[CLIENT]: message not provided");
            return;
        }

        Connection rabbitConn = null;

        try {
            String message = new ObjectMapper().writeValueAsString(generateMessage(proccessId, payload, success));
            log.info("[CLIENT]: generated message: " + message);

            log.info("[CLIENT]: trying to setup rabbitmq connection");
            rabbitConn = this.connectToRabbitMQ();

            Channel channel = getChannel(rabbitConn);

            channel.basicPublish(
                    EXCHANGE_NAME,
                    this.getRoutingKey(success),
                    null,
                    message.getBytes(StandardCharsets.UTF_8)
            );

            log.info(String.format("[CLIENT]: successfully published '%s' to rabbit", message));
        } catch (Exception e) {
            // global exception since task is async
            log.error("[CLIENT]: publishing failed because of ==> ", e);
        } finally {
            if (rabbitConn != null) {
                log.info("[CLIENT]: closing rabbit connection");
                rabbitConn.close();
            }
            log.info("[CLIENT]: publish successfully finished");
        }
    }

    private Channel getChannel(Connection rabbitConn) throws IOException {
        Channel channel = rabbitConn.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "direct", true);
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY_SUCCESS);
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY_FAILURE);

        return channel;
    }

    private Connection connectToRabbitMQ() throws URISyntaxException, NoSuchAlgorithmException, KeyManagementException,
            TimeoutException, IOException {
        String rabbitUri = String.format("amqp://%s:%s@%s:%s/%s",
                System.getenv("RABBITMQ_USER"),
                System.getenv("RABBITMQ_PASSWORD"),
                System.getenv("RABBITMQ_HOST"),
                System.getenv("RABBITMQ_PORT"),
                System.getenv("RABBITMQ_VHOST"));

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setUri(rabbitUri);
        return connectionFactory.newConnection();

    }

    private MessageBody generateMessage(String processId, String message, boolean success) {
        String statusMessage = this.getRoutingKey(success);
        return new MessageBody(statusMessage, processId, message, LocalDateTime.now().toString());
    }

}

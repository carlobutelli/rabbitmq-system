package com.queue.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.queue.api.messages.MessageBody;
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
import java.util.concurrent.TimeoutException;

public class MessageProducer {

    private static final Logger log = LoggerFactory.getLogger(MessageProducer.class);

    private final String QUEUE_NAME = "hello";
    private final String EXCHANGE_NAME = "greetings";
    private final String ROUTING_KEY_SUCCESS = "got_success";
    private final String ROUTING_KEY_FAILURE = "got_failure";

    public MessageProducer() {
    }

    private String getRoutingKey(boolean success) {
        if (success) return "got_success";
        return "got_failure";
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
                    getRoutingKey(success),
                    null,
                    message.getBytes(StandardCharsets.UTF_8)
            );

            log.info(String.format("[CLIENT]: successfully published '%s' to rabbit", message));
        } catch (Exception e) {
            // global exception since task is async
            log.error("[CLIENT]: failed because of ", e);
        } finally {
            if (rabbitConn != null) {
                log.info("[CLIENT]: closing rabbit connection");
                rabbitConn.close();
            }
            log.info("[CLIENT]: successfully finished");
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
                System.getenv("RABBIT_USER"),
                System.getenv("RABBIT_PASSWORD"),
                System.getenv("RABBIT_HOST"),
                System.getenv("RABBIT_PORT"),
                System.getenv("RABBIT_VHOST"));

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setUri(rabbitUri);
        return connectionFactory.newConnection();

    }

    private MessageBody generateMessage(String processId, String message, boolean success) {
        String statusMessage = this.getRoutingKey(success);
        return new MessageBody(statusMessage, processId, message);
    }

}

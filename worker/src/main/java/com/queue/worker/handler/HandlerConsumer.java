package com.queue.worker.handler;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

public class HandlerConsumer extends DefaultConsumer {
    private static Logger log = LoggerFactory.getLogger(HandlerConsumer.class);
    private final Channel channel;

    public HandlerConsumer(Channel channel) {
        super(channel);
        this.channel = channel;
    }

    @Override
    public void handleDelivery(String consumerTag,
                               Envelope envelope,
                               AMQP.BasicProperties properties,
                               byte[] body) throws IOException {

        String transactionId = UUID.randomUUID().toString();
        long deliveryTag = envelope.getDeliveryTag();
        String message = new String(body);
        log.info(
                String.format("[%s] channel %s: received message %s...",
                        transactionId, consumerTag, message.substring(0,10))
        );
        try {
            channel.basicAck(deliveryTag, false);
            log.info(String.format("[%s] message consumed correctly", transactionId));
        }
        catch(Exception e){
            log.error(String.format("[%s] handling threw error %s", transactionId, e.getMessage()));
            channel.basicReject(deliveryTag, true);
        }
        finally {
            log.info(String.format("[%s] queue processing finished", transactionId));
        }
    }
}

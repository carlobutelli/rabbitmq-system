package com.queue.publisher.controllers;

import com.queue.publisher.api.payload.MessagePayload;
import com.queue.publisher.api.responses.ErrorResponse;
import com.queue.publisher.client.MessageProducer;
import com.queue.publisher.api.responses.BaseResponse;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@RestController
@Api(value = "Publisher of messages to RabbitMQ")
public class PublisherController {

    private static final Logger log = LoggerFactory.getLogger(PublisherController.class);
    private static final String MODULE = "PUBLISH";

    @PostMapping("/publish")
    @ApiOperation(value = "Route to publish the message")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "successful", response = BaseResponse.class),
            @ApiResponse(code = 500, message = "error", response = ErrorResponse.class)
    })
    public ResponseEntity publishMessage(
            @ApiParam(hidden = true) @RequestHeader(value = "x-request-id") String transactionId,
            @NotNull @RequestBody MessagePayload mp) {
        log.info(String.format("[%s] %s: got new request to publish the message", MODULE, transactionId));

        try {
            handleCallBackMessage(mp.getMessage(), transactionId, mp.isSuccess());
            return new ResponseEntity<>(
                    new BaseResponse(
                            transactionId,
                            "OK",
                            "Message sent correctly",
                            HttpStatus.INTERNAL_SERVER_ERROR.value()),
                    HttpStatus.OK);
        } catch (Exception e) {
            handleCallBackMessage(mp.getMessage(), transactionId, mp.isSuccess());
            return handleException(e, "Error publishing the message", transactionId);
        }
    }

    private void handleCallBackMessage(String payload, String transactionId, boolean success) {
        if (payload != null) {
            new Thread(() -> {
                try {
                    log.info(String.format("[PUBLISH]: %s publish message into the queue", transactionId));
                    new MessageProducer().putMessageIntoQueue(transactionId, payload, success);
                } catch (Exception e) {
                    log.error(String.format("[PUBLISH]: %s fatal error while trying to publish", transactionId), e);
                }
            }).start();
        }
        if (!success) {
            log.error(String.format("[PUBLISH]: %s success is false", transactionId));
        }
    }

    private ResponseEntity handleException(Exception e, String errorDescription, String transactionId) {
        log.error(String.format("[PUBLISH] %s: %s  %s", transactionId, errorDescription, e.getMessage()));
        return new ResponseEntity<>(
                new BaseResponse(
                        transactionId,
                        "ERROR",
                        "internal server error",
                        HttpStatus.INTERNAL_SERVER_ERROR.value()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

}

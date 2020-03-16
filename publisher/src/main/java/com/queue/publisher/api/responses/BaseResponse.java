package com.queue.publisher.api.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;

@ApiModel(value = "BaseResponse", description = "basic response object")
public class BaseResponse {

    private String transactionId;
    private String status;
    private String message;
    private int statusCode;


    public BaseResponse() {
    }

    public BaseResponse(String transactionId, String status, String message, int statusCode) {
        this.transactionId = transactionId;
        this.status = status;
        this.message = message;
        this.statusCode = statusCode;
    }

    @JsonProperty
    public int getStatusCode() {
        return statusCode;
    }

    @JsonProperty
    public String getMessage() {
        return message;
    }

    @JsonProperty
    public String getStatus() {
        return status;
    }

    public String getTransactionId() {
        return transactionId;
    }
}


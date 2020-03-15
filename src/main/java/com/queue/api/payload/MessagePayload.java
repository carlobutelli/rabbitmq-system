package com.queue.api.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class MessagePayload {

    @NotNull
    @JsonProperty("message")
    private String message;

    @NotNull
    @JsonProperty("success")
    private boolean success;

    public MessagePayload() {}

    public MessagePayload(String message, boolean success) {

        this.message = message;
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}

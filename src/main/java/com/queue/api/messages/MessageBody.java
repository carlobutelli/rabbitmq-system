package com.queue.api.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageBody {

    @JsonProperty("status")
    private String status;

    @JsonProperty("process_id")
    private String processId;

    @JsonProperty("message")
    private String message;

    @JsonCreator
    public MessageBody() {
    }

    public MessageBody(String status, String processId, String message) {
        this.status = status;
        this.processId = processId;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }

    public String getProcessId() {
        return processId;
    }
}

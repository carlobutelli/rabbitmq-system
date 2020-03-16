package com.queue.publisher.api.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageBody {

    @JsonProperty("status")
    private String status;

    @JsonProperty("process_id")
    private String processId;

    @JsonProperty("message")
    private String message;

    @JsonProperty("date")
    private String date;

    @JsonCreator
    public MessageBody() {
    }

    public MessageBody(String status, String processId, String message, String ldt) {
        setStatus(status);
        setProcessId(processId);
        setMessage(message);
        setDate(ldt);
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

    public String getDate() {
        return date;
    }

    public void setDate(String ldt) {
        this.date = ldt;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

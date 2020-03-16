package com.queue.publisher.exceptions;

import java.util.Date;

public class BaseResponseError {

    private String transactionId;
    private Date timestamp;
    private String message;
    private String details;

    public BaseResponseError(String transactionId, Date timestamp, String message, String details) {
        super();
        this.transactionId = transactionId;
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
    }

    public String getTransactionId() {
        return this.transactionId;
    }

    public Date getTimestamp() { return this.timestamp; }

    public String getMessage() { return this.message; }

    public String getDetails() { return this.details; }
}
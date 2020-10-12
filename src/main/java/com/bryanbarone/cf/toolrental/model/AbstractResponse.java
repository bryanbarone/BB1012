package com.bryanbarone.cf.toolrental.model;


public class AbstractResponse {

    public final static String STATUS_OK = "OK";
    public final static String STATUS_VALIDATION = "VALIDATION";
    public final static String STATUS_ERROR = "ERROR";
    public final static String MESSAGE_OK = "Success";

    private String status;
    private String message;
    private String field;
    private Object result;


    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public AbstractResponse() {
        // default to OK / Success response
        this.status = STATUS_OK;
        this.message = MESSAGE_OK;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}

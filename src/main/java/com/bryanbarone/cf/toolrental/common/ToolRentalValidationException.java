package com.bryanbarone.cf.toolrental.common;


public class ToolRentalValidationException extends Throwable{

    private String field;

    public ToolRentalValidationException(String message, String field) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}

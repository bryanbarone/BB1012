package com.bryanbarone.cf.toolrental.model;


public class Order {

    private String toolCode;
    private String rentalDays;
    private String discount;
    private String checkoutDate;


    public Order(String toolCode, int rentalDays, int discount, String checkoutDate) {
        this.toolCode = toolCode;
        this.rentalDays = "" + rentalDays;
        this.discount = "" + discount;
        this.checkoutDate = checkoutDate;
    }

    public String getToolCode() {
        return toolCode;
    }

    public void setToolCode(String toolCode) {
        this.toolCode = toolCode;
    }

    public String getRentalDays() {
        return rentalDays;
    }

    public void setRentalDays(String rentalDays) {
        this.rentalDays = rentalDays;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getCheckoutDate() {
        return checkoutDate;
    }

    public void setCheckoutDate(String checkoutDate) {
        this.checkoutDate = checkoutDate;
    }

    @Override
    public String toString() {
        return "Order{" +
            "toolCode='" + toolCode + '\'' +
            ", rentalDays='" + rentalDays + '\'' +
            ", discount='" + discount + '\'' +
            ", checkoutDate='" + checkoutDate + '\'' +
            '}';
    }
}

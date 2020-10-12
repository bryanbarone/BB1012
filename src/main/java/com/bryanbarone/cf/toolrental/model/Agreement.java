package com.bryanbarone.cf.toolrental.model;

import java.math.BigDecimal;


public class Agreement {

    private String toolCode;
    private int rentalDays;
    private int discount;
    private String checkoutDate;
    private String toolType;
    private String toolBrand;
    private String returnDate;
    private int chargeDays; // number of chargeable days (excludes "no charge days")
    private BigDecimal rentalCharge;
    private BigDecimal preDiscountAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;


    public String getToolCode() {
        return toolCode;
    }

    public void setToolCode(String toolCode) {
        this.toolCode = toolCode;
    }

    public int getRentalDays() {
        return rentalDays;
    }

    public void setRentalDays(int rentalDays) {
        this.rentalDays = rentalDays;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public String getCheckoutDate() {
        return checkoutDate;
    }

    public void setCheckoutDate(String checkoutDate) {
        this.checkoutDate = checkoutDate;
    }

    public String getToolType() {
        return toolType;
    }

    public void setToolType(String toolType) {
        this.toolType = toolType;
    }

    public String getToolBrand() {
        return toolBrand;
    }

    public void setToolBrand(String toolBrand) {
        this.toolBrand = toolBrand;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public int getChargeDays() {
        return chargeDays;
    }

    public void setChargeDays(int chargeDays) {
        this.chargeDays = chargeDays;
    }

    public BigDecimal getRentalCharge() {
        return rentalCharge;
    }

    public void setRentalCharge(BigDecimal rentalCharge) {
        this.rentalCharge = rentalCharge;
    }

    public BigDecimal getPreDiscountAmount() {
        return preDiscountAmount;
    }

    public void setPreDiscountAmount(BigDecimal preDiscountAmount) {
        this.preDiscountAmount = preDiscountAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(BigDecimal finalAmount) {
        this.finalAmount = finalAmount;
    }

    @Override
    public String toString() {
        return "Agreement{" +
                "toolCode='" + toolCode + '\'' +
                ", rentalDays=" + rentalDays +
                ", discount=" + discount +
                ", checkoutDate='" + checkoutDate + '\'' +
                ", toolType='" + toolType + '\'' +
                ", toolBrand='" + toolBrand + '\'' +
                ", returnDate='" + returnDate + '\'' +
                ", chargeDays=" + chargeDays +
                ", rentalCharge=" + rentalCharge +
                ", preDiscountAmount=" + preDiscountAmount +
                ", discountAmount=" + discountAmount +
                ", finalAmount=" + finalAmount +
                '}';
    }
}

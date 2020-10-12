package com.bryanbarone.cf.toolrental.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
public class RentalCost {
    @Id
    String tooltype_id;
    BigDecimal charge;
    boolean weekday;  // is there a weekday charge
    boolean weekend;  // is there a weekend charge
    boolean holiday;  // is there a holiday charge (independence day / labor day)


    public String getTooltype_id() {
        return tooltype_id;
    }

    public void setTooltype_id(String tooltype_id) {
        this.tooltype_id = tooltype_id;
    }

    public BigDecimal getCharge() {
        return charge;
    }

    public void setCharge(BigDecimal charge) {
        this.charge = charge;
    }

    public boolean isWeekday() {
        return weekday;
    }

    public void setWeekday(boolean weekday) {
        this.weekday = weekday;
    }

    public boolean isWeekend() {
        return weekend;
    }

    public void setWeekend(boolean weekend) {
        this.weekend = weekend;
    }

    public boolean isHoliday() {
        return holiday;
    }

    public void setHoliday(boolean holiday) {
        this.holiday = holiday;
    }

}

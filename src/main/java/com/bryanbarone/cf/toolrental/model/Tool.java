package com.bryanbarone.cf.toolrental.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class Tool {
    @Id
    private String code;
    private String type;
    private String brand;

    @Transient
    private RentalCost costs;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String tooltype) {
        this.type = tooltype;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public RentalCost getCosts() {
        return costs;
    }

    public void setCosts(RentalCost costs) {
        this.costs = costs;
    }
}

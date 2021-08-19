package com.udacity.pricing.domain.price;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;

/**
 * Represents the price of a given vehicle, including currency.
 */
@Entity
public class Price {

    private String currency;
    private BigDecimal price;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Price() {
    }

    public Price(String currency, BigDecimal price, Long id) {
        this.currency = currency;
        this.price = price;
        this.id = id;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Long getVehicleId() {
        return id;
    }

    public void setVehicleId(Long vehicleId) {
        this.id = vehicleId;
    }
}

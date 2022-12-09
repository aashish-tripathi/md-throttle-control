package com.clsa.md.throttlecontrol;

import java.time.LocalDateTime;

public class MarketData {

    private String symbol;
    private double price;
    private LocalDateTime updateTime;

    public MarketData() {
    }

    public MarketData(String symbol, double price, LocalDateTime updateTime) {
        this.symbol = symbol;
        this.price = price;
        this.updateTime = updateTime;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "MarketData{" +
                "symbol='" + symbol + '\'' +
                ", price=" + price +
                ", updateTime=" + updateTime +
                '}';
    }
}

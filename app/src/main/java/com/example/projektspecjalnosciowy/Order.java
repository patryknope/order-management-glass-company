package com.example.projektspecjalnosciowy;

public class Order {

    private String order;
    private String measurement;
    private String count;
    private String glass;
    private String id;
    private String date;

    public Order() {
    }

    public Order(String order, String measurement, String count, String glass, String id, String date) {
        this.order = order;
        this.measurement = measurement;
        this.count = count;
        this.glass = glass;
        this.id=id;
        this.date = date;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getGlass() {
        return glass;
    }

    public void setGlass(String phoneNumber) {
        this.glass = glass;
    }

    public String getId(){return id;}

    public void setId(){this.id=id;}


}

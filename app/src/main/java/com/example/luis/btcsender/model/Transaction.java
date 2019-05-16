package com.example.luis.btcsender.model;

public class Transaction {

    private Double btc;
    private Double fee;
    private String date;
    private String address;
    private String id;

    public Transaction(Double btc, Double fee, String date, String address, String id) {
        this.btc = btc;
        this.fee = fee;
        this.date = date;
        this.address = address;
        this.id = id;
    }

    public Double getBtc() {
        return btc;
    }

    public void setBtc(Double btc) {
        this.btc = btc;
    }

    public String getDate() {
        return date;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}

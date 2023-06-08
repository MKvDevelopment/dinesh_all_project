package com.wheel.colorgame.Model;

public class HistoryModel {

    private String time;
    private String status;
    private String amount;
    private String charge;
    private String method;

    public HistoryModel(String time, String status, String amount, String charge, String method) {
        this.time = time;
        this.status = status;
        this.amount = amount;
        this.charge = charge;
        this.method = method;
    }

    public HistoryModel() {
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}

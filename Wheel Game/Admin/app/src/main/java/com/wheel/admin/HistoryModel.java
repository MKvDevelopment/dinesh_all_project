package com.wheel.admin;

public class HistoryModel {

    private String type;
    private String amount;
    private String method;
    private String uid;
    private String item_id;
    private String status;
    private String time;

    public HistoryModel(String type, String amount, String method, String uid, String item_id, String status, String time) {
        this.type = type;
        this.amount = amount;
        this.method = method;
        this.uid = uid;
        this.item_id = item_id;
        this.status = status;
        this.time = time;
    }

    public HistoryModel() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
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

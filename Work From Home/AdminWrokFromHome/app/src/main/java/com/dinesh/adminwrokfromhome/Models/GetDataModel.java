package com.dinesh.adminwrokfromhome.Models;

import com.google.firebase.Timestamp;

public class GetDataModel {

    private String plan;
    private String wallet;
    private String email;
    private Timestamp time;

    public GetDataModel(String plan, String wallet, String email, Timestamp time) {
        this.plan = plan;
        this.wallet = wallet;
        this.email = email;
        this.time = time;
    }

    public GetDataModel() {
    }

    public Timestamp getTime() {
        return time;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

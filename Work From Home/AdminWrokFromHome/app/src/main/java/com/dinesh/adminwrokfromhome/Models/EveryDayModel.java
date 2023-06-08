package com.dinesh.adminwrokfromhome.Models;

public class EveryDayModel {
    private String email;
    private String plan_start_date;
    private String wallet;
    private String view_post;
    private String reason;
    private String status;

    public EveryDayModel() {
    }

    public EveryDayModel(String email, String plan_start_date, String wallet, String view_post, String reason, String status) {
        this.email = email;
        this.plan_start_date = plan_start_date;
        this.wallet = wallet;
        this.view_post = view_post;
        this.reason = reason;
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public String getStatus() {
        return status;
    }

    public String getEmail() {
        return email;
    }

    public String getPlan_start_date() {
        return plan_start_date;
    }

    public String getWallet() {
        return wallet;
    }

    public String getView_post() {
        return view_post;
    }
}

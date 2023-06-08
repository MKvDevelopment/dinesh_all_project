package com.dinesh.adminwrokfromhome.Models;

public class Product_item_model {

    private String email;
    private String plan_start_date;
    private String plan_end_date;
    private String wallet;
    private String view_post;
    private String status;
    private String reason;

    public Product_item_model() {
    }

    public Product_item_model(String email, String plan_start_date, String plan_end_date, String wallet, String view_post, String status,String reason) {
        this.email = email;
        this.plan_start_date = plan_start_date;
        this.plan_end_date = plan_end_date;
        this.wallet = wallet;
        this.view_post = view_post;
        this.status = status;
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public String getEmail() {
        return email;
    }

    public String getStatus() {
        return status;
    }

    public String getPlan_start_date() {
        return plan_start_date;
    }

    public String getPlan_end_date() {
        return plan_end_date;
    }

    public String getWallet() {
        return wallet;
    }

    public String getView_post() {
        return view_post;
    }
}

package com.typingwork.admintypingwork.Model;

public class WithdrawModel {
    private String amount,status;
    private String id, email, mobile, name, upi;

    public WithdrawModel(String amount,String status, String id, String email, String mobile, String name, String upi) {
        this.amount = amount;
        this.id = id;
        this.status = status;
        this.email = email;
        this.mobile = mobile;
        this.name = name;
        this.upi = upi;
    }

    public WithdrawModel() {
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUpi() {
        return upi;
    }

    public void setUpi(String upi) {
        this.upi = upi;
    }
}

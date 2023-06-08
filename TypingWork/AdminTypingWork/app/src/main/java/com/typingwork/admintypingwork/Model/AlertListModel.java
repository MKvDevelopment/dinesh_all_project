package com.typingwork.admintypingwork.Model;

public class AlertListModel {

    private String wallet,uid,time,email,name,photo;

    public AlertListModel(String wallet, String uid, String time, String email, String name, String photo) {
        this.wallet = wallet;
        this.uid = uid;
        this.time = time;
        this.email = email;
        this.name = name;
        this.photo = photo;
    }

    public AlertListModel() {
    }

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}

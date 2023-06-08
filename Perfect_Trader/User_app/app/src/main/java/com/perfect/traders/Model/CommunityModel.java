package com.perfect.traders.Model;

public class CommunityModel {

    private String title, des, img, time, uid;

    public CommunityModel(String title, String des, String img, String time, String uid) {
        this.title = title;
        this.des = des;
        this.img = img;
        this.uid = uid;
        this.time = time;
    }

    public CommunityModel() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

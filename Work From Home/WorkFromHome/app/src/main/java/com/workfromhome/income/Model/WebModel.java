package com.workfromhome.income.Model;

import androidx.annotation.Keep;

@Keep
public class WebModel {

    private String web_income;
    private String web_time;
    private String web_title;
    private String weblink;
    private String web_id;
    private boolean isDone=false;

    public WebModel(String web_income, String web_time, String web_title, String weblink, String web_id, boolean isDone) {
        this.web_income = web_income;
        this.web_time = web_time;
        this.web_title = web_title;
        this.weblink = weblink;
        this.web_id = web_id;
        this.isDone = isDone;
    }

    public WebModel() {
    }

    public String getWeb_id() {
        return web_id;
    }

    public void setWeb_id(String web_id) {
        this.web_id = web_id;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public String getWeb_income() {
        return web_income;
    }

    public void setWeb_income(String web_income) {
        this.web_income = web_income;
    }

    public String getWeb_time() {
        return web_time;
    }

    public void setWeb_time(String web_time) {
        this.web_time = web_time;
    }

    public String getWeb_title() {
        return web_title;
    }

    public void setWeb_title(String web_title) {
        this.web_title = web_title;
    }

    public String getWeblink() {
        return weblink;
    }

    public void setWeblink(String weblink) {
        this.weblink = weblink;
    }
}

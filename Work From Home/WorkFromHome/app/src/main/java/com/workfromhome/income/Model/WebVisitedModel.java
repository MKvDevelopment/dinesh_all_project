package com.workfromhome.income.Model;

import androidx.annotation.Keep;

@Keep
public class WebVisitedModel {

    private String web_id;
    private boolean isDone;

    public WebVisitedModel(String web_id, boolean isDone) {
        this.web_id = web_id;
        this.isDone = isDone;
    }

    public WebVisitedModel() {
    }

    public String getWeb_id() {
        return web_id;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public void setWeb_id(String web_id) {
        this.web_id = web_id;
    }
}

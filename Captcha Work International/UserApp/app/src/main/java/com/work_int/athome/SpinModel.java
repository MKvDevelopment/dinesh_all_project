package com.work_int.athome;

public class SpinModel {


    String uid;
    String winning_balence;
    String index;

    public SpinModel(String uid, String winning_balence, String index) {
        this.uid = uid;
        this.winning_balence = winning_balence;
        this.index = index;
    }

    public SpinModel() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getWinning_balence() {
        return winning_balence;
    }

    public void setWinning_balence(String winning_balence) {
        this.winning_balence = winning_balence;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }
}

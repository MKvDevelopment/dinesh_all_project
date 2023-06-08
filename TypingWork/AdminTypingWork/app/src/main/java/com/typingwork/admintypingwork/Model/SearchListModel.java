package com.typingwork.admintypingwork.Model;

public class SearchListModel {

    private String name,wallet,withdraw,photo,uid,
            activation,block,wrong_entry,right_entry;


    public SearchListModel(String name,String uid, String wallet, String withdraw, String photo, String activation, String block, String wrong_entry, String right_entry) {
        this.uid = uid;
        this.name = name;
        this.wallet = wallet;
        this.withdraw = withdraw;
        this.photo = photo;
        this.activation = activation;
        this.block = block;
        this.wrong_entry = wrong_entry;
        this.right_entry = right_entry;
    }

    public SearchListModel() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    public String getWithdraw() {
        return withdraw;
    }

    public void setWithdraw(String withdraw) {
        this.withdraw = withdraw;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getActivation() {
        return activation;
    }

    public void setActivation(String activation) {
        this.activation = activation;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getWrong_entry() {
        return wrong_entry;
    }

    public void setWrong_entry(String wrong_entry) {
        this.wrong_entry = wrong_entry;
    }

    public String getRight_entry() {
        return right_entry;
    }

    public void setRight_entry(String right_entry) {
        this.right_entry = right_entry;
    }
}

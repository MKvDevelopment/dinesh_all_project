package com.typingwork.jobathome.Model;

public class UserDataModel {

    private String email, phone, name, block, bReason, wrong_entry, right_entry, rating, ads_activation, friend_uid,
            instal1, instal2, refer_code, refered_by, plan, wallet, activation, form_price, photo, withdraw, device_id;

    public UserDataModel(String email,String device_id, String friend_uid, String ads_activation, String withdraw, String photo, String phone, String name, String block, String bReason, String wrong_entry, String right_entry, String rating, String instal1, String instal2, String refer_code, String refered_by, String plan, String wallet, String activation, String form_price) {
        this.ads_activation = ads_activation;
        this.photo = photo;
        this.device_id = device_id;
        this.friend_uid = friend_uid;
        this.withdraw = withdraw;
        this.email = email;
        this.phone = phone;
        this.name = name;
        this.block = block;
        this.bReason = bReason;
        this.wrong_entry = wrong_entry;
        this.right_entry = right_entry;
        this.rating = rating;
        this.instal1 = instal1;
        this.instal2 = instal2;
        this.refer_code = refer_code;
        this.refered_by = refered_by;
        this.plan = plan;
        this.wallet = wallet;
        this.activation = activation;
        this.form_price = form_price;
    }

    public UserDataModel() {
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getFriend_uid() {
        return friend_uid;
    }

    public void setFriend_uid(String friend_uid) {
        this.friend_uid = friend_uid;
    }

    public String getAds_activation() {
        return ads_activation;
    }

    public void setAds_activation(String ads_activation) {
        this.ads_activation = ads_activation;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getbReason() {
        return bReason;
    }

    public void setbReason(String bReason) {
        this.bReason = bReason;
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

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getInstal1() {
        return instal1;
    }

    public void setInstal1(String instal1) {
        this.instal1 = instal1;
    }

    public String getInstal2() {
        return instal2;
    }

    public void setInstal2(String instal2) {
        this.instal2 = instal2;
    }

    public String getRefer_code() {
        return refer_code;
    }

    public void setRefer_code(String refer_code) {
        this.refer_code = refer_code;
    }

    public String getRefered_by() {
        return refered_by;
    }

    public void setRefered_by(String refered_by) {
        this.refered_by = refered_by;
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

    public String getActivation() {
        return activation;
    }

    public void setActivation(String activation) {
        this.activation = activation;
    }

    public String getForm_price() {
        return form_price;
    }

    public void setForm_price(String form_price) {
        this.form_price = form_price;
    }
}

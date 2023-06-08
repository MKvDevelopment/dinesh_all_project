package com.workz.athome.Model.UserData;

public class Data {

    private String activation,captcha_price,email,mobile,uid,userName,wallet,instant_activation,install,index,winning_balence,remove_ads,token,is_button_pressed;

    public Data(String activation, String captcha_price, String email, String mobile, String uid, String userName, String wallet, String instant_activation, String install, String index, String winning_balence, String remove_ads, String token, String is_button_pressed) {
        this.activation = activation;
        this.captcha_price = captcha_price;
        this.email = email;
        this.mobile = mobile;
        this.uid = uid;
        this.userName = userName;
        this.wallet = wallet;
        this.instant_activation = instant_activation;
        this.install = install;
        this.index = index;
        this.winning_balence = winning_balence;
        this.remove_ads = remove_ads;
        this.token = token;
        this.is_button_pressed = is_button_pressed;
    }

    public Data() {
    }

    public String getIs_button_pressed() {
        return is_button_pressed;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getActivation() {
        return activation;
    }

    public void setActivation(String activation) {
        this.activation = activation;
    }

    public String getCaptcha_price() {
        return captcha_price;
    }

    public void setCaptcha_price(String captcha_price) {
        this.captcha_price = captcha_price;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    public String getInstant_activation() {
        return instant_activation;
    }

    public void setInstant_activation(String instant_activation) {
        this.instant_activation = instant_activation;
    }

    public String getInstall() {
        return install;
    }

    public void setInstall(String install) {
        this.install = install;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getWinning_balence() {
        return winning_balence;
    }

    public void setWinning_balence(String winning_balence) {
        this.winning_balence = winning_balence;
    }

    public String getRemove_ads() {
        return remove_ads;
    }

    public void setRemove_ads(String remove_ads) {
        this.remove_ads = remove_ads;
    }
}

package com.skincarestudio.skincarestudioadmin.Model;

public class WebDataModel {

    String product_title;
    String product_des;
    String sb_title;
    String product_img;

    public WebDataModel(String product_title, String product_des, String sb_title, String product_img) {
        this.product_title = product_title;
        this.product_des = product_des;
        this.sb_title = sb_title;
        this.product_img = product_img;
    }

    public WebDataModel() {
    }

    public String getSb_title() {
        return sb_title;
    }

    public void setSb_title(String sb_title) {
        this.sb_title = sb_title;
    }

    public String getProduct_title() {
        return product_title;
    }

    public void setProduct_title(String product_title) {
        this.product_title = product_title;
    }

    public String getProduct_des() {
        return product_des;
    }

    public void setProduct_des(String product_des) {
        this.product_des = product_des;
    }

    public String getProduct_img() {
        return product_img;
    }

    public void setProduct_img(String product_img) {
        this.product_img = product_img;
    }
}



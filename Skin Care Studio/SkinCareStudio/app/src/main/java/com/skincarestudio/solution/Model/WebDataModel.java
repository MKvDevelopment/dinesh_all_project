package com.skincarestudio.solution.Model;

public class WebDataModel {

    private String product_title;
    private String product_des;
    private String sb_title;
    private String product_img;

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

    public String getProduct_title() {
        return product_title;
    }

    public String getProduct_des() {
        return product_des;
    }

    public String getProduct_img() {
        return product_img;
    }
}



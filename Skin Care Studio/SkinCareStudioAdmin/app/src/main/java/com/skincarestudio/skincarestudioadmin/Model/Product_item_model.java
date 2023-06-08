package com.skincarestudio.skincarestudioadmin.Model;

public class Product_item_model {

    private String product_img;
    private String product_title;
    private String link;
    private String sb_title;
    private String product_des;
    private String product_price;


    public Product_item_model() {
    }

    public Product_item_model(String product_img, String product_title, String link, String sb_title, String product_des, String product_price) {
        this.product_img = product_img;
        this.product_title = product_title;
        this.link = link;
        this.sb_title = sb_title;
        this.product_des = product_des;
        this.product_price = product_price;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getSb_title() {
        return sb_title;
    }

    public void setSb_title(String sb_title) {
        this.sb_title = sb_title;
    }

    public String getProduct_img() {
        return product_img;
    }

    public void setProduct_img(String product_img) {
        this.product_img = product_img;
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

    public String getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }
}

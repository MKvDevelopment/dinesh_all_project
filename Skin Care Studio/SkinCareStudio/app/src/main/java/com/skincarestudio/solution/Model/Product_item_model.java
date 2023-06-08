package com.skincarestudio.solution.Model;

public class Product_item_model {

    private String product_img;
    private String product_title;
    private String sb_title;
    private String link;
    private String product_des;
    private String product_price;


    public Product_item_model() {
    }

    public Product_item_model(String product_img, String product_title, String sb_title, String link, String product_des, String product_price) {
        this.product_img = product_img;
        this.product_title = product_title;
        this.sb_title = sb_title;
        this.link = link;
        this.product_des = product_des;
        this.product_price = product_price;
    }

    public String getSb_title() {
        return sb_title;
    }

    public String getLink() {
        return link;
    }

    public String getProduct_img() {
        return product_img;
    }

    public String getProduct_title() {
        return product_title;
    }

    public String getProduct_des() {
        return product_des;
    }

    public String getProduct_price() {
        return product_price;
    }
}

package com.document.security.model;

public class C_Model {
    private String title;
    private String dis;
    private String type;
    private String link;

    public C_Model() {
    }

    public C_Model(String title, String dis, String type, String link) {
        this.title = title;
        this.dis = dis;
        this.type = type;
        this.link = link;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDis() {
        return dis;
    }

    public void setDis(String dis) {
        this.dis = dis;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

package com.document.security.model;

public class F_Model {
    private String name;
    private String dis;
    private String type;

    public F_Model() {
    }

    public F_Model(String name, String dis, String type) {
        this.name = name;
        this.dis = dis;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDis() {
        return dis;
    }

    public void setDis(String dis) {
        this.dis = dis;
    }
}


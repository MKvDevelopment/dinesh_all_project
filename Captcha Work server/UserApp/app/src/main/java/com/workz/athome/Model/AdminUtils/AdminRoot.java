package com.workz.athome.Model.AdminUtils;

public class AdminRoot {
    private boolean status;
    Data data;
    private String message;

    public AdminRoot(boolean status, Data data, String message) {
        this.status = status;
        data = data;
        this.message = message;
    }

    public AdminRoot() {
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data dataObject) {
        data = dataObject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

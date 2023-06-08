package com.workz.athome.Model.UserData;

public class Root {

    public String message;
    public Data data;
    public boolean status;

    public Root(String message, String token, Data data, boolean status) {
        this.message = message;
        this.data = data;
        this.status = status;
    }

    public Root() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}

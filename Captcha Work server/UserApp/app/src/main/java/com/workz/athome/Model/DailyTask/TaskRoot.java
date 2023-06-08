package com.workz.athome.Model.DailyTask;

public class TaskRoot {
    private boolean status;
    private Data data;
    private String message;

    public TaskRoot(boolean status, Data data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public TaskRoot() {
    }

    public boolean isStatus() {
        return status;
    }

    public Data getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}

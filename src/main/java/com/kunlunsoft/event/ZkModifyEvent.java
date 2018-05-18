package com.kunlunsoft.event;

public class ZkModifyEvent {
    private String path;
    private String oldValue;

    public ZkModifyEvent(String path, String oldValue) {
        this.path = path;
        this.oldValue = oldValue;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }
}

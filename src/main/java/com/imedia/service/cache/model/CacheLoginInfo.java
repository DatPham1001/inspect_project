package com.imedia.service.cache.model;

import java.util.List;

public class CacheLoginInfo {
    private String username;
    private List<String> deviceIds;

    public CacheLoginInfo(String username, List<String> deviceIds) {
        this.username = username;
        this.deviceIds = deviceIds;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getDeviceIds() {
        return deviceIds;
    }

    public void setDeviceIds(List<String> deviceIds) {
        this.deviceIds = deviceIds;
    }
}

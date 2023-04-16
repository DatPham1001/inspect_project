package com.imedia.service.user.model;

public class UserInfoAPIResponse {
    private Integer status;
    private String message;
    private UserInfoResponse data;

    public UserInfoAPIResponse(Integer status, String message, UserInfoResponse data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public UserInfoAPIResponse(Integer status, String message) {
        this.status = status;
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserInfoResponse getData() {
        return data;
    }

    public void setData(UserInfoResponse data) {
        this.data = data;
    }
}

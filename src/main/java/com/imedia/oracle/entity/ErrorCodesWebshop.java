package com.imedia.oracle.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ERROR_CODES_WEBSHOP")

public class ErrorCodesWebshop {
    @Id
    private Integer id;
    @Column(name = "ERROR_CODE")
    private Integer errorCode;
    private String message;
    private String comments;
    private Integer type;
    @Column(name = "EXTERNAL_ERROR_CODE")
    private Integer externalErrorCode;

    public ErrorCodesWebshop() {
    }

    public ErrorCodesWebshop(Integer errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public Integer getExternalErrorCode() {
        return externalErrorCode;
    }

    public void setExternalErrorCode(Integer externalErrorCode) {
        this.externalErrorCode = externalErrorCode;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}

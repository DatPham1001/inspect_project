package com.imedia.service.wallet.model;

import org.apache.logging.log4j.LogManager;

import java.util.logging.Logger;

public class WalletBaseRequest {

    private int p_code;
    private String data;
    private String response_data;
    private Integer sessionExpectLifeTime;
    // = hexString = tripleDes(data)
    private String signature;
    private String service_code;// : dạng guid
    private int client_type = 3;
    // quy định client thuộc loại gì:
    // 1: client master(dịch vụ chủ)
    // 2: client của dịch vụ master dạng: web site
    // 3: client của dịch vụ master dạng: android app
    // 4: client của dịch vụ master dạng: ios app
    // 5: client của dịch vụ master dạng: winfone
    private String client_version = "1.0.0";// version của client.

    public WalletBaseRequest(int p_code, String data, String response_data, String signature, String service_code) {
        this.p_code = p_code;
        this.data = data;
        this.response_data = response_data;
        this.signature = signature;
        this.service_code = service_code;
    }

    public Integer getSessionExpectLifeTime() {
        return sessionExpectLifeTime;
    }

    public void setSessionExpectLifeTime(Integer sessionExpectLifeTime) {
        this.sessionExpectLifeTime = sessionExpectLifeTime;
    }

    public int getP_code() {
        return p_code;
    }

    public void setP_code(int p_code) {
        this.p_code = p_code;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getResponse_data() {
        return response_data;
    }

    public void setResponse_data(String response_data) {
        this.response_data = response_data;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getService_code() {
        return service_code;
    }

    public void setService_code(String service_code) {
        this.service_code = service_code;
    }

    public int getClient_type() {
        return client_type;
    }

    public void setClient_type(int client_type) {
        this.client_type = client_type;
    }

    public String getClient_version() {
        return client_version;
    }

    public void setClient_version(String client_version) {
        this.client_version = client_version;
    }
}

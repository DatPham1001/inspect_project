package com.imedia.service.wallet.model;

import java.util.List;

public class WalletBaseResponse {
    private Integer p_code;
    private String data;
    private Integer status;
    private String response_identify;
    private String response_msg;
    private String signature;
    private String service_code;
    private String client_version;
    private Integer client_type;
    private Long balBefore;
    private Long balAfter;
    private Long holdingBalBefore;
    private Long holdingBalAfter;
    private List<CheckPendingInfo> lstQueryLog;

    public List<CheckPendingInfo> getLstQueryLog() {
        return lstQueryLog;
    }

    public void setLstQueryLog(List<CheckPendingInfo> lstQueryLog) {
        this.lstQueryLog = lstQueryLog;
    }

    public WalletBaseResponse(Integer status) {
        this.status = status;
    }

    public Integer getP_code() {
        return p_code;
    }

    public void setP_code(Integer p_code) {
        this.p_code = p_code;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getResponse_identify() {
        return response_identify;
    }

    public void setResponse_identify(String response_identify) {
        this.response_identify = response_identify;
    }

    public String getResponse_msg() {
        return response_msg;
    }

    public void setResponse_msg(String response_msg) {
        this.response_msg = response_msg;
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

    public String getClient_version() {
        return client_version;
    }

    public void setClient_version(String client_version) {
        this.client_version = client_version;
    }

    public Integer getClient_type() {
        return client_type;
    }

    public void setClient_type(Integer client_type) {
        this.client_type = client_type;
    }

    public Long getBalBefore() {
        return balBefore;
    }

    public void setBalBefore(Long balBefore) {
        this.balBefore = balBefore;
    }

    public Long getBalAfter() {
        return balAfter;
    }

    public void setBalAfter(Long balAfter) {
        this.balAfter = balAfter;
    }

    public Long getHoldingBalBefore() {
        return holdingBalBefore;
    }

    public void setHoldingBalBefore(Long holdingBalBefore) {
        this.holdingBalBefore = holdingBalBefore;
    }

    public Long getHoldingBalAfter() {
        return holdingBalAfter;
    }

    public void setHoldingBalAfter(Long holdingBalAfter) {
        this.holdingBalAfter = holdingBalAfter;
    }
}

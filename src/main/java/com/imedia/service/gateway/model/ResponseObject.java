package com.imedia.service.gateway.model;

import java.util.HashMap;
import java.util.Map;

public class ResponseObject {
    private String partnerCode;
    private DataObject data;

    public ResponseObject(String partnerCode, DataObject data) {
        this.partnerCode = partnerCode;
        this.data = data;
    }

    public ResponseObject() {
        super();
        // TODO Auto-generated constructor stub
    }


    public String getPartnerCode() {
        return partnerCode;
    }

    public void setPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }

    public DataObject getData() {
        return data;
    }

    public void setData(DataObject data) {
        this.data = data;
    }
}

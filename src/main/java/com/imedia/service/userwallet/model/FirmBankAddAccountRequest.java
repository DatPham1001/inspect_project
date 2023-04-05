package com.imedia.service.userwallet.model;

public class FirmBankAddAccountRequest {
    //    {
//        "RequestId": "IMD45",
//            "RequestTime": "2020-09-24 16:56:20",
//            "PartnerCode": "PARTNERTEST",
//            "Operation": 9001,
//            "BankNo": "970415",
//            "AccNo": "116002687168",
//            "Signature": "",
//            "AccType": "0"
//    }
    private String RequestId;
    private String RequestTime;
    private String PartnerCode;
    private Integer Operation;
    private String BankNo;
    private String AccNo;
    private String Signature = "";
    private String AccType = "0";

    public String getRequestId() {
        return RequestId;
    }

    public void setRequestId(String requestId) {
        RequestId = requestId;
    }

    public String getRequestTime() {
        return RequestTime;
    }

    public void setRequestTime(String requestTime) {
        RequestTime = requestTime;
    }

    public String getPartnerCode() {
        return PartnerCode;
    }

    public void setPartnerCode(String partnerCode) {
        PartnerCode = partnerCode;
    }

    public Integer getOperation() {
        return Operation;
    }

    public void setOperation(Integer operation) {
        Operation = operation;
    }

    public String getBankNo() {
        return BankNo;
    }

    public void setBankNo(String bankNo) {
        BankNo = bankNo;
    }

    public String getAccNo() {
        return AccNo;
    }

    public void setAccNo(String accNo) {
        AccNo = accNo;
    }

    public String getSignature() {
        return Signature;
    }

    public void setSignature(String signature) {
        Signature = signature;
    }

    public String getAccType() {
        return AccType;
    }

    public void setAccType(String accType) {
        AccType = accType;
    }
}

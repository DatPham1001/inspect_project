package com.imedia.service.userwallet.model;

public class FirmBankAddAccountResponse {
    //    {
//        "RequestId": "IMD45",
//            "RequestTime": "2020-09-24 16:56:20",
//            "PartnerCode": "PARTNERTEST",
//            "Operation": 9001,
//            "BankNo": "970415",
//            "AccNo": "1160026871618",
//            "Signature": "",
//            "AccType": "0",
//            "RequestAmount": "0",
//            "ResponseCode": 11,
//            "ResponseMessage": "QUERY FAIL, SYSTEM UNAVAILABLE NOW",
//            "AccName": ""
//    }
    private String RequestId;
    private String RequestTime;
    private String PartnerCode;
    private Integer Operation;
    private String BankNo;
    private String Signature;
    private String AccType;
    private String RequestAmount;
    private Integer ResponseCode;
    private String ResponseMessage;
    private String AccName;

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

    public String getRequestAmount() {
        return RequestAmount;
    }

    public void setRequestAmount(String requestAmount) {
        RequestAmount = requestAmount;
    }

    public Integer getResponseCode() {
        return ResponseCode;
    }

    public void setResponseCode(Integer responseCode) {
        ResponseCode = responseCode;
    }

    public String getResponseMessage() {
        return ResponseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        ResponseMessage = responseMessage;
    }

    public String getAccName() {
        return AccName;
    }

    public void setAccName(String accName) {
        AccName = accName;
    }
}

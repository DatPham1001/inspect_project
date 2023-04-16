package com.imedia.service.wallet.model;

import java.math.BigDecimal;

public class CheckPendingInfo {
    //     "balanceLogId": 228813,
//             "balChangeType": 4100,
//             "balChangeStatus": 200,
//             "balChangeAmount": 100000,
//             "balBef": 50520000,
//             "balAf": 50620000,
//             "changeDate": "2021/07/16 15:55:06",
//             "latestChange": "2021/07/16 15:55:06",
//             "acceptBy": "0946482511",
//             "createdBy": "0946482511",
//             "requestFromIp": "N/A",
//             "remark": ",CONG TAM GIU:0946482511 SO TIEN:100000",
//             "listBillType": 0,
//             "targetAccount": "N/A",
//             "authAt": "2021/07/16 15:55:06",
//             "accId": 4706,
//             "requestIdentity": "35b7a8a67c044c5fa7acd103aa6268c840862",
//             "clientReqId": "WDB_164123853021",
//             "serviceId": 1,
//             "clientSubReqId": "N/A",
//             "balanceSubType": 0,
//             "balanceStackCode": "3696c56a1836f1c2bef27f2c35e4dbe7",
//             "bankCode": "970415",
//             "bankName": "N/A",
//             "accBankName": "CUSTOMER TITLE 1",
//             "accBankCode": "116002687168",
//             "bankAccType": 0,
//             "autoWithdrawal": 1,
//             "firmStatus": 0,
//             "apprRejWithdrawal": 0
    private Integer firmStatus;
    private String accBankName;
    private String accBankCode;
    private String bankCode;
    private BigDecimal balBef;
    private BigDecimal balAf;

    public BigDecimal getBalBef() {
        return balBef;
    }

    public void setBalBef(BigDecimal balBef) {
        this.balBef = balBef;
    }

    public BigDecimal getBalAf() {
        return balAf;
    }

    public void setBalAf(BigDecimal balAf) {
        this.balAf = balAf;
    }

    public Integer getFirmStatus() {
        return firmStatus;
    }

    public void setFirmStatus(Integer firmStatus) {
        this.firmStatus = firmStatus;
    }

    public String getAccBankName() {
        return accBankName;
    }

    public void setAccBankName(String accBankName) {
        this.accBankName = accBankName;
    }

    public String getAccBankCode() {
        return accBankCode;
    }

    public void setAccBankCode(String accBankCode) {
        this.accBankCode = accBankCode;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }
}

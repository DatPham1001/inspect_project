package com.imedia.service.wallet.model;

import java.math.BigDecimal;
import java.util.List;

public class WalletBalanceRequest {
    //    {
//        "session_key": "[login success session]",
//            "money_active": 0,
//            "bal_stack_code": "3696c56a1836f1c2bef27f2c35e4dbe7",
//            "balChangeType": 0,
//            "balChangeStatus": 0,
//            "balChangeAmount": số tiền muốn giao dịch,
//            "createdBy": "sdt",
//            "clientReqId": "mã định danh giao dịch của client",
//            "balance_sub_type": "mã phụ tham chiếu ở mục *6*"
//        "client_sub_request_id": "mã giao dịch phụ của client, có thể truyền vào là mã vận đơn để tra cứu được trên ví"
//        "bal_stack_code":[mã ngăn ví]
//    }
    private String session_key = "";
    private String bal_stack_code;
    private BigDecimal money_active = BigDecimal.ZERO;
    private Long accId;
    private BigDecimal balChangeType = BigDecimal.ZERO;
    private BigDecimal balChangeStatus = BigDecimal.ZERO;
    private BigDecimal balChangeAmount;
    private String createdBy;
    private String client_request_id;
    private String clientReqId;
    private String balance_sub_type;
    private String client_sub_request_id;
    private List<String> list_query_client_id;

    public WalletBalanceRequest() {
    }

    public String getClient_request_id() {
        return client_request_id;
    }

    public void setClient_request_id(String client_request_id) {
        this.client_request_id = client_request_id;
    }

    public List<String> getList_query_client_id() {
        return list_query_client_id;
    }

    public void setList_query_client_id(List<String> list_query_client_id) {
        this.list_query_client_id = list_query_client_id;
    }

    public Long getAccId() {
        return accId;
    }

    public void setAccId(Long accId) {
        this.accId = accId;
    }

    public BigDecimal getBalChangeAmount() {
        return balChangeAmount;
    }

    public void setBalChangeAmount(BigDecimal balChangeAmount) {
        this.balChangeAmount = balChangeAmount;
    }

    public String getSession_key() {
        return session_key;
    }

    public void setSession_key(String session_key) {
        this.session_key = session_key;
    }

    public String getBal_stack_code() {
        return bal_stack_code;
    }

    public void setBal_stack_code(String bal_stack_code) {
        this.bal_stack_code = bal_stack_code;
    }

    public BigDecimal getMoney_active() {
        return money_active;
    }

    public void setMoney_active(BigDecimal money_active) {
        this.money_active = money_active;
    }

    public BigDecimal getBalChangeType() {
        return balChangeType;
    }

    public void setBalChangeType(BigDecimal balChangeType) {
        this.balChangeType = balChangeType;
    }

    public BigDecimal getBalChangeStatus() {
        return balChangeStatus;
    }

    public void setBalChangeStatus(BigDecimal balChangeStatus) {
        this.balChangeStatus = balChangeStatus;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getClientReqId() {
        return clientReqId;
    }

    public void setClientReqId(String clientReqId) {
        this.clientReqId = clientReqId;
    }

    public String getBalance_sub_type() {
        return balance_sub_type;
    }

    public void setBalance_sub_type(String balance_sub_type) {
        this.balance_sub_type = balance_sub_type;
    }

    public String getClient_sub_request_id() {
        return client_sub_request_id;
    }

    public void setClient_sub_request_id(String client_sub_request_id) {
        this.client_sub_request_id = client_sub_request_id;
    }
}

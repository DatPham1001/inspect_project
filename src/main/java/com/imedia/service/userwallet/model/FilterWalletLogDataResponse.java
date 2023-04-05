package com.imedia.service.userwallet.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FilterWalletLogDataResponse {
    private Long id;
    private String orderId;
    private String reason;
    private Long type;
    private BigDecimal changeBase;
    private BigDecimal cost;
    private Long status;
    private Date createAt;
    private Date uTimeStamp;
    private String code;
    private BigDecimal toBase;
    private String orderIdWS;

    public FilterWalletLogDataResponse(Long id, String orderId, String reason, Long type, BigDecimal changeBase, BigDecimal cost, Long status, Date createAt, Date uTimeStamp, String code, BigDecimal toBase) {
        this.id = id;
        this.orderId = orderId;
        this.reason = reason;
        this.type = type;
        this.changeBase = changeBase;
        this.cost = cost;
        this.status = status;
        this.createAt = createAt;
        this.uTimeStamp = uTimeStamp;
        this.code = code;
        this.toBase = toBase;
    }
//    public FilterWalletLogDataResponse(Long id, String orderId, String reason, Long type, BigDecimal changeBase, BigDecimal cost, Long status, Date createAt, Date uTimeStamp, String code, BigDecimal toBase) {
//        this.id = id;
//        this.orderId = orderId;
//        this.reason = reason;
//        this.type = type;
//        this.changeBase = changeBase;
//        this.cost = cost;
//        this.status = status;
//        this.createAt = createAt;
//        this.uTimeStamp = uTimeStamp;
//        this.code = code;
//        this.toBase = toBase;
//    }
//
//    public BigDecimal getToBase() {
//        return toBase;
//    }
//
//    public void setToBase(BigDecimal toBase) {
//        this.toBase = toBase;
//    }
//
//    public String getCode() {
//        return code;
//    }
//
//    public void setCode(String code) {
//        this.code = code;
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getOrderId() {
//        return orderId;
//    }
//
//    public void setOrderId(String orderId) {
//        this.orderId = orderId;
//    }
//
//    public String getReason() {
//        return reason;
//    }
//
//    public void setReason(String reason) {
//        this.reason = reason;
//    }
//
//    public Long getType() {
//        return type;
//    }
//
//    public void setType(Long type) {
//        this.type = type;
//    }
//
//    public BigDecimal getChangeBase() {
//        return changeBase;
//    }
//
//    public void setChangeBase(BigDecimal changeBase) {
//        this.changeBase = changeBase;
//    }
//
//    public BigDecimal getCost() {
//        return cost;
//    }
//
//    public void setCost(BigDecimal cost) {
//        this.cost = cost;
//    }
//
//    public Long getStatus() {
//        return status;
//    }
//
//    public void setStatus(Long status) {
//        this.status = status;
//    }
//
//    public Date getCreateAt() {
//        return createAt;
//    }
//
//    public void setCreateAt(Date createAt) {
//        this.createAt = createAt;
//    }
//
//    public Date getuTimeStamp() {
//        return uTimeStamp;
//    }
//
//    public void setuTimeStamp(Date uTimeStamp) {
//        this.uTimeStamp = uTimeStamp;
//    }
}

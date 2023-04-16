package com.imedia.oracle.entity;

import com.imedia.service.userwallet.model.FilterWalletLogDataResponse;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;


/**
 * The persistent class for the WALLET_LOGS database table.
 */
@Entity
@Table(name = "WALLET_LOGS")
public class WalletLog implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
//	@SequenceGenerator(name = "ISEQ$$_74127", sequenceName = "ISEQ$$_74127", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private BigDecimal balance;

    @Column(name = "CHANGE_BASE")
    private BigDecimal changeBase;

    @Column(name = "CHANGE_BORROW")
    private BigDecimal changeBorrow;

    @Column(name = "CHANGE_HOLD")
    private BigDecimal changeHold;

    @Column(name = "CHANGE_PROMOTION")
    private BigDecimal changePromotion;

    @Column(name = "CHANGE_ROOT")
    private BigDecimal changeRoot;
    @Column(name = "BANK_ACCOUNT")
    private String bankAccount;
    private String code;

    private BigDecimal cost;

    @CreationTimestamp
    @Column(name = "CREATE_AT", updatable = false)
    private Timestamp createAt;

    @Column(name = "CREATE_BY")
    private BigDecimal createBy;

    @Column(name = "ERROR_CODE")
    private String errorCode;

    @Lob
    @Column(name = "ERROR_DETAIL")
    private String errorDetail;

    @Column(name = "FROM_BASE")
    private BigDecimal fromBase;

    @Column(name = "FROM_BORROW")
    private BigDecimal fromBorrow;

    @Column(name = "FROM_HOLD")
    private BigDecimal fromHold;

    @Column(name = "FROM_PROMOTION")
    private BigDecimal fromPromotion;

    @Column(name = "ORDER_DETAIL_ID")
    private BigDecimal orderDetailId;

    @Column(name = "ORDER_ID")
    private BigDecimal orderId;

    private String reason;

    private BigDecimal status;

    @Column(name = "TO_BASE")
    private BigDecimal toBase;

    @Column(name = "TO_BORROW")
    private BigDecimal toBorrow;

    @Column(name = "TO_HOLD")
    private BigDecimal toHold;

    @Column(name = "TO_PROMOTION")
    private BigDecimal toPromotion;

    @Column(name = "TYPE")
    private BigDecimal type;

    @UpdateTimestamp
    @Column(name = "UTIMESTAMP")
    private Timestamp utimestamp;

    @Column(name = "WALLET_ID")
    private BigDecimal walletId;

    @Column(name = "MEMO")
    private String memo;

    @Column(name = "BAL_STACK_CODE")
    private String balStackCode;
    @Column(name = "IS_DELETED")
    private Integer isDeleted;

    public WalletLog() {
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getBalStackCode() {
        return balStackCode;
    }

    public void setBalStackCode(String balStackCode) {
        this.balStackCode = balStackCode;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        return this.balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getChangeBase() {
        return this.changeBase;
    }

    public void setChangeBase(BigDecimal changeBase) {
        this.changeBase = changeBase;
    }

    public BigDecimal getChangeBorrow() {
        return this.changeBorrow;
    }

    public void setChangeBorrow(BigDecimal changeBorrow) {
        this.changeBorrow = changeBorrow;
    }

    public BigDecimal getChangeHold() {
        return this.changeHold;
    }

    public void setChangeHold(BigDecimal changeHold) {
        this.changeHold = changeHold;
    }

    public BigDecimal getChangePromotion() {
        return this.changePromotion;
    }

    public void setChangePromotion(BigDecimal changePromotion) {
        this.changePromotion = changePromotion;
    }

    public BigDecimal getChangeRoot() {
        return this.changeRoot;
    }

    public void setChangeRoot(BigDecimal changeRoot) {
        this.changeRoot = changeRoot;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getCost() {
        return this.cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public Timestamp getCreateAt() {
        return this.createAt;
    }

    public void setCreateAt(Timestamp createAt) {
        this.createAt = createAt;
    }

    public BigDecimal getCreateBy() {
        return this.createBy;
    }

    public void setCreateBy(BigDecimal createBy) {
        this.createBy = createBy;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDetail() {
        return this.errorDetail;
    }

    public void setErrorDetail(String errorDetail) {
        this.errorDetail = errorDetail;
    }

    public BigDecimal getFromBase() {
        return this.fromBase;
    }

    public void setFromBase(BigDecimal fromBase) {
        this.fromBase = fromBase;
    }

    public BigDecimal getFromBorrow() {
        return this.fromBorrow;
    }

    public void setFromBorrow(BigDecimal fromBorrow) {
        this.fromBorrow = fromBorrow;
    }

    public BigDecimal getFromHold() {
        return this.fromHold;
    }

    public void setFromHold(BigDecimal fromHold) {
        this.fromHold = fromHold;
    }

    public BigDecimal getFromPromotion() {
        return this.fromPromotion;
    }

    public void setFromPromotion(BigDecimal fromPromotion) {
        this.fromPromotion = fromPromotion;
    }

    public BigDecimal getOrderDetailId() {
        return this.orderDetailId;
    }

    public void setOrderDetailId(BigDecimal orderDetailId) {
        this.orderDetailId = orderDetailId;
    }

    public BigDecimal getOrderId() {
        return this.orderId;
    }

    public void setOrderId(BigDecimal orderId) {
        this.orderId = orderId;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public BigDecimal getStatus() {
        return this.status;
    }

    public void setStatus(BigDecimal status) {
        this.status = status;
    }

    public BigDecimal getToBase() {
        return this.toBase;
    }

    public void setToBase(BigDecimal toBase) {
        this.toBase = toBase;
    }

    public BigDecimal getToBorrow() {
        return this.toBorrow;
    }

    public void setToBorrow(BigDecimal toBorrow) {
        this.toBorrow = toBorrow;
    }

    public BigDecimal getToHold() {
        return this.toHold;
    }

    public void setToHold(BigDecimal toHold) {
        this.toHold = toHold;
    }

    public BigDecimal getToPromotion() {
        return this.toPromotion;
    }

    public void setToPromotion(BigDecimal toPromotion) {
        this.toPromotion = toPromotion;
    }

    public BigDecimal getType() {
        return this.type;
    }

    public void setType(BigDecimal type) {
        this.type = type;
    }

    public Timestamp getUtimestamp() {
        return this.utimestamp;
    }

    public void setUtimestamp(Timestamp utimestamp) {
        this.utimestamp = utimestamp;
    }

    public BigDecimal getWalletId() {
        return this.walletId;
    }

    public void setWalletId(BigDecimal walletId) {
        this.walletId = walletId;
    }

}
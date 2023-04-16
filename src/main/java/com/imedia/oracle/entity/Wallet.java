package com.imedia.oracle.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;


/**
 * The persistent class for the WALLETS database table.
 */
@Entity
@Table(name = "WALLETS")
@NamedQuery(name = "Wallet.findAll", query = "SELECT w FROM Wallet w")
public class Wallet implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
//    @SequenceGenerator(name = "ISEQ$$_74073", sequenceName = "ISEQ$$_74073", allocationSize = 1)
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ISEQ$$_74073")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", insertable = false)
    private long id;

    @Column(name = "ACCOUNT_EPURSE_ID")
    private BigDecimal accountEpurseId;

    private BigDecimal base;

    private BigDecimal hold;

    @Column(name = "MAX_BORROW")
    private BigDecimal maxBorrow;

    private BigDecimal promotion;

    private BigDecimal ship;

    private BigDecimal shop;

    private BigDecimal status;

    @Column(name = "USER_ID")
    private BigDecimal userId;

    public Wallet() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getAccountEpurseId() {
        return this.accountEpurseId;
    }

    public void setAccountEpurseId(BigDecimal accountEpurseId) {
        this.accountEpurseId = accountEpurseId;
    }

    public BigDecimal getBase() {
        return this.base;
    }

    public void setBase(BigDecimal base) {
        this.base = base;
    }

    public BigDecimal getHold() {
        return this.hold;
    }

    public void setHold(BigDecimal hold) {
        this.hold = hold;
    }

    public BigDecimal getMaxBorrow() {
        return this.maxBorrow;
    }

    public void setMaxBorrow(BigDecimal maxBorrow) {
        this.maxBorrow = maxBorrow;
    }

    public BigDecimal getPromotion() {
        return this.promotion;
    }

    public void setPromotion(BigDecimal promotion) {
        this.promotion = promotion;
    }

    public BigDecimal getShip() {
        return this.ship;
    }

    public void setShip(BigDecimal ship) {
        this.ship = ship;
    }

    public BigDecimal getShop() {
        return this.shop;
    }

    public void setShop(BigDecimal shop) {
        this.shop = shop;
    }

    public BigDecimal getStatus() {
        return this.status;
    }

    public void setStatus(BigDecimal status) {
        this.status = status;
    }

    public BigDecimal getUserId() {
        return this.userId;
    }

    public void setUserId(BigDecimal userId) {
        this.userId = userId;
    }

}
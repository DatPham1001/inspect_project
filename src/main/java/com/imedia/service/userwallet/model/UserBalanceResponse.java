package com.imedia.service.userwallet.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UserBalanceResponse {
    //    private BigDecimal
    private String username;
    private BigDecimal remainBalance = BigDecimal.ZERO;
    private BigDecimal debt = BigDecimal.ZERO;
    private BigDecimal holdBalance = BigDecimal.ZERO;
    private BigDecimal availableBalance = BigDecimal.ZERO;
    private BigDecimal creditBalance = BigDecimal.ZERO;
    private BigDecimal point = BigDecimal.ZERO;
    private BigDecimal availableHold = BigDecimal.ZERO;
}

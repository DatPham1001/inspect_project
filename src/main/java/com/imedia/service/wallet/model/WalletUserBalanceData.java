package com.imedia.service.wallet.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class WalletUserBalanceData {
    private String bal_code;
    private BigDecimal remain_balance;
    private BigDecimal debt_balance_before;
    private BigDecimal advance_balance_before;
    private BigDecimal holding_balance;
    private String latest_update;


}

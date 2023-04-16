package com.imedia.service.gateway.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ChangeCodRequest {
    private String order_id;
    private String order_code;
    private BigDecimal cod_amount;
    private int confirm_type;
    private int id_account_partner;
}

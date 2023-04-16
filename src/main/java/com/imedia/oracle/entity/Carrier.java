package com.imedia.oracle.entity;

import com.imedia.service.postage.model.ForWardConfigOM;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;


/**
 * The persistent class for the CARRIERS database table.
 */
@Entity
@Table(name = "CARRIERS")
@SqlResultSetMapping(
        name = "TargetConfigMapping",
        classes = {
                @ConstructorResult(
                        targetClass = ForWardConfigOM.class,
                        columns = {
                                @ColumnResult(name = "id", type = Integer.class),
                                @ColumnResult(name = "code", type = String.class),
                                @ColumnResult(name = "carrierPackCode", type = String.class),
                                @ColumnResult(name = "priceSettingId", type = Integer.class),
                                @ColumnResult(name = "url", type = String.class),
                                @ColumnResult(name = "specialServices", type = String.class),
                                @ColumnResult(name = "isHola", type = BigDecimal.class),
                        }
                )
        }
)
@Getter
@Setter
public class Carrier implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private long id;


    private String code;

    @Column(name = "CREATED_AT")
    private Timestamp createdAt;

    private String description;

    @Column(name = "FEE_URL")
    private String feeUrl;
    @Column(name = "COD_URL")
    private String codUrl;
    @Column(name = "GATEWAY_URL")
    private String gatewayUrl;

    @Column(name = "IS_HOLA")
    private BigDecimal isHola;

    @Column(name = "IS_USED")
    private BigDecimal isUsed;

    private String name;

    private BigDecimal status;

    private Timestamp utimestamp;
//1 : cho phép giao 1 phần
//2: cho phép người nhận trả phí
//3: cho phép thanh toán tiền mặt
//4: cho phép giao lại
//5 : cho phép chuyển hoàn

    @Column(name = "SPECIAL_SERVICES")
    private String specialServices;


}
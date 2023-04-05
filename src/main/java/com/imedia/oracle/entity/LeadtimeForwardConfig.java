package com.imedia.oracle.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;


/**
 * The persistent class for the LEADTIME_FORWARD_CONFIGS database table.
 */
@Entity
@Table(name = "LEADTIME_FORWARD_CONFIGS")
@Getter
@Setter
@NamedQuery(name = "LeadtimeForwardConfig.findAll", query = "SELECT l FROM LeadtimeForwardConfig l")
public class LeadtimeForwardConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private long id;

    @Column(name = "CARRIER_CODE")
    private String carrierCode;

    @Column(name = "CARRIER_ID")
    private BigDecimal carrierId;

    @Column(name = "CARRIER_PACK_CODE")
    private String carrierPackCode;

    @Column(name = "CARRIER_PACK_ID")
    private BigDecimal carrierPackId;

    @Column(name = "FROM_TIME")
    private Timestamp fromTime;

    @Column(name = "IS_DELETE")
    private Integer isDelete;

    private String note;

    @Column(name = "PACK_CODE")
    private String packCode;

    @Column(name = "PACK_ID")
    private Integer packId;

    @Column(name = "PACK_ROUTE_ID")
    private Integer packRouteId;

    @Column(name = "PRICE_SETTING_ID")
    private Integer priceSettingId;

    private Integer rank;

    @Column(name = "TO_TIME")
    private Timestamp toTime;

    private Timestamp utimestamp;

    public LeadtimeForwardConfig() {
    }


}
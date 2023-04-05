package com.imedia.oracle.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;


/**
 * The persistent class for the ADDRESS_DELIVERY database table.
 */
@Entity
@Table(name = "ADDRESS_DELIVERY")
@Getter
@Setter
public class AddressDelivery implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String address;

    @Column(name = "DISTRICT_CODE")
    private String districtCode;

    @Column(name = "DISTRICT_ID")
    private BigDecimal districtId;

    private Double latitude;

    private Double longitude;

    @Column(name = "PROVINCE_CODE")
    private String provinceCode;

    @Column(name = "PROVINCE_ID")
    private BigDecimal provinceId;

    @Column(name = "STRING_FILTER")
    private String stringFilter;

    @Column(name = "WARD_CODE")
    private String wardCode;

    @Column(name = "WARD_ID")
    private BigDecimal wardId;
    @Column(name = "PROVINCE_NAME")
    private String provinceName;
    @Column(name = "DISTRICT_NAME")
    private String districtName;
    @Column(name = "WARD_NAME")
    private String wardName;


}
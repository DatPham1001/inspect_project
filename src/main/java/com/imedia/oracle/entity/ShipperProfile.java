package com.imedia.oracle.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;


/**
 * The persistent class for the SHIPPER_PROFILES database table.
 */
@Entity
@Getter
@Setter
@Table(name = "SHIPPER_PROFILES")
@NamedQuery(name = "ShipperProfile.findAll", query = "SELECT s FROM ShipperProfile s")
public class ShipperProfile implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private long id;

    @Column(name = "CONTACT_ADDRESS")
    private String contactAddress;

    @Column(name = "CONTACT_NAME")
    private String contactName;

    @Column(name = "CONTACT_PHONE")
    private String contactPhone;

    @Column(name = "CONTACT_RELATIONSHIP")
    private String contactRelationship;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_ID_CARD")
    private Date dateIdCard;

    @Column(name = "DRIVER_LICENSE")
    private String driverLicense;

    @Temporal(TemporalType.DATE)
    @Column(name = "DRIVER_LICENSE_EX_DATE")
    private Date driverLicenseExDate;

    @Column(name = "IDENTITY_VERIFICATION_TYPE")
    private BigDecimal identityVerificationType;

    @Lob
    @Column(name = "IMG_AFTER_DRIVER_LICENSE")
    private String imgAfterDriverLicense;

    @Lob
    @Column(name = "IMG_AFTER_DRIVER_REGIST")
    private String imgAfterDriverRegist;

    @Lob
    @Column(name = "IMG_AFTER_ID_CARD")
    private String imgAfterIdCard;

    @Lob
    @Column(name = "IMG_AFTER_INSURRANCE")
    private String imgAfterInsurrance;

    @Lob
    @Column(name = "IMG_BEFORE_DRIVER_LICENSE")
    private String imgBeforeDriverLicense;

    @Lob
    @Column(name = "IMG_BEFORE_DRIVER_REGIST")
    private String imgBeforeDriverRegist;

    @Lob
    @Column(name = "IMG_BEFORE_ID_CARD")
    private String imgBeforeIdCard;

    @Lob
    @Column(name = "IMG_BEFORE_INSURRANCE")
    private String imgBeforeInsurrance;

    @Column(name = "IMG_IDENTITY_VERIFICATION_ID")
    private BigDecimal imgIdentityVerificationId;

    @Temporal(TemporalType.DATE)
    @Column(name = "INSURANCE_EX_DATE")
    private Date insuranceExDate;

    @Column(name = "LICENSE_PLATE")
    private String licensePlate;

    @Column(name = "NUMBER_ID_CARD")
    private String numberIdCard;

    @Column(name = "PLACE_ID_CARD")
    private String placeIdCard;

    @Column(name = "PROFILE_ID")
    private Long profileId;

    @Column(name = "REQUESTED_TIME")
    private Timestamp requestedTime;

    @Column(name = "SPECIES_LICENSE_TYPE")
    private BigDecimal speciesLicenseType;

    private BigDecimal status;

    @Column(name = "TYPE")
    private BigDecimal type;

    @Column(name = "UPDATED_TIME")
    private Timestamp updatedTime;

    private Timestamp utimestamp;

    @Column(name = "VEHICLE_TYPE")
    private BigDecimal vehicleType;

    public ShipperProfile() {
    }


}
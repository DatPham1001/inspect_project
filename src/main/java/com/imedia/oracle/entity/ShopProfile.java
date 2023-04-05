package com.imedia.oracle.entity;

import com.imedia.service.user.model.UserInfoAddressData;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;


/**
 * The persistent class for the SHOP_PROFILES database table.
 */
@Entity
@Table(name = "SHOP_PROFILES")
@SqlResultSetMapping(
        name = "UserInfoAddressDataMapping",
        classes = {
                @ConstructorResult(
                        targetClass = UserInfoAddressData.class,
                        columns = {
                                @ColumnResult(name = "provinceName", type = String.class),
                                @ColumnResult(name = "provinceCode", type = String.class),
                                @ColumnResult(name = "districtName", type = String.class),
                                @ColumnResult(name = "districtCode", type = String.class),
                                @ColumnResult(name = "wardName", type = String.class),
                                @ColumnResult(name = "wardCode", type = String.class),
                                @ColumnResult(name = "address", type = String.class),
                        }
                )
        }
)
@SqlResultSetMapping(
        name = "CacheAddressMapping",
        classes = {
                @ConstructorResult(
                        targetClass = UserInfoAddressData.class,
                        columns = {
                                @ColumnResult(name = "PROVINCE_NAME", type = String.class),
                                @ColumnResult(name = "PROVINCE_CODE", type = String.class),
                                @ColumnResult(name = "DISTRICT_NAME", type = String.class),
                                @ColumnResult(name = "DISTRICT_CODE", type = String.class),
                                @ColumnResult(name = "WARD_NAME", type = String.class),
                                @ColumnResult(name = "WARD_CODE", type = String.class),
                        }
                )
        }
)
public class ShopProfile implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String address;

    @Column(name = "APP_USER_ID")
    private Long appUserId;
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATE_AT", updatable = false)
    private Date createAt;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_ID_CARD")
    private Date dateIdCard;

    @Column(name = "EMAIL_BILL")
    private String emailBill;

    @Column(name = "NUMBER_ID_CARD")
    private String numberIdCard;

    @Column(name = "PHONE_OTP")
    private String phoneOtp;

    @Column(name = "PLACE_ID_CARD")
    private String placeIdCard;

    private Integer status;
    @UpdateTimestamp
    private Timestamp utimestamp;
    @Column(name = "PROVINCE_CODE")
    private String provinceCode;
    @Column(name = "DISTRICT_CODE")
    private String districtCode;
    @Column(name = "WARD_CODE")
    private String wardCode;

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }

    public String getWardCode() {
        return wardCode;
    }

    public void setWardCode(String wardCode) {
        this.wardCode = wardCode;
    }

    public ShopProfile() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getAppUserId() {
        return this.appUserId;
    }

    public void setAppUserId(Long appUserId) {
        this.appUserId = appUserId;
    }

    public Date getCreateAt() {
        return this.createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Date getDateIdCard() {
        return this.dateIdCard;
    }

    public void setDateIdCard(Date dateIdCard) {
        this.dateIdCard = dateIdCard;
    }

    public String getEmailBill() {
        return this.emailBill;
    }

    public void setEmailBill(String emailBill) {
        this.emailBill = emailBill;
    }

    public String getNumberIdCard() {
        return this.numberIdCard;
    }

    public void setNumberIdCard(String numberIdCard) {
        this.numberIdCard = numberIdCard;
    }

    public String getPhoneOtp() {
        return this.phoneOtp;
    }

    public void setPhoneOtp(String phoneOtp) {
        this.phoneOtp = phoneOtp;
    }

    public String getPlaceIdCard() {
        return this.placeIdCard;
    }

    public void setPlaceIdCard(String placeIdCard) {
        this.placeIdCard = placeIdCard;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Timestamp getUtimestamp() {
        return this.utimestamp;
    }

    public void setUtimestamp(Timestamp utimestamp) {
        this.utimestamp = utimestamp;
    }

}
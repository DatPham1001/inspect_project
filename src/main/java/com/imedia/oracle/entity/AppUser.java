package com.imedia.oracle.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;


/**
 * The persistent class for the APP_USERS database table.
 */
@Entity
@Table(name = "APP_USERS")
public class AppUser implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
//    @SequenceGenerator(name = "ISEQ$$_73923", sequenceName = "ISEQ$$_73923", allocationSize = 1)
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ISEQ$$_73923")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", insertable = false)
    private long id;

    @Column(name = "ACCESS_TOKEN")
    private String accessToken;

    @Column(name = "ACCOUNT_CODE")
    private String accountCode;

    @Column(name = "ADDRESS_ID")
    private BigDecimal addressId;

    private String avatar;

    @Temporal(TemporalType.DATE)
    private Date birthday;

    @Column(name = "CALLBACK_URL")
    private String callbackUrl;

    private String company;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATE_AT", updatable = false)
    private Date createAt;

    private String email;

    @Column(name = "FACEBOOK_ID")
    private String facebookId;

    private String identify;

    @Column(name = "IDENTIFY_ROLE_TYPE")
    private Integer identifyRoleType;

    @Column(name = "IDENTITY_CARD")
    private String identityCard;

    private String name;

    private BigDecimal partner = BigDecimal.ZERO;

    private String password;

    private String phone;

    private Integer point = 0;

    @Column(name = "PROFILE_LEVEL")
    private Integer profileLevel;

    private Integer rating;

    private Integer sex = 3;

    private Integer ship = 0;

    private Integer shop = 0;

    public Integer getOtpType() {
        return otpType;
    }

    public void setOtpType(Integer otpType) {
        this.otpType = otpType;
    }

    private Integer otpType = 0;

    private String types;

    @Column(name = "USER_GROUP_ID")
    private Integer userGroupId = 0;

    @UpdateTimestamp
    @Column(name = "UTIMESTAMP")
    private Timestamp utimestamp;

    @Column(name = "VA_ID")
    private String vaId;

    @Column(name = "VA_TOKEN")
    private String vaToken;

    @Column(name = "WALLET_SHIP_ID")
    private BigDecimal walletShipId = BigDecimal.ZERO;

    @Column(name = "WALLET_SHOP_ID")
    private BigDecimal walletShopId = BigDecimal.ZERO;

    @Column(name = "ACCOUNT_EPURSE_ID")
    private BigDecimal accountEpurseId;
    @Column(name = "ALLOW_WITHDRAW")
    private Integer allowWithdraw;

    public AppUser() {
    }

    public Integer getAllowWithdraw() {
        return allowWithdraw;
    }

    public void setAllowWithdraw(Integer allowWithdraw) {
        this.allowWithdraw = allowWithdraw;
    }

    public BigDecimal getAccountEpurseId() {
        return accountEpurseId;
    }

    public void setAccountEpurseId(BigDecimal accountEpurseId) {
        this.accountEpurseId = accountEpurseId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public BigDecimal getAddressId() {
        return addressId;
    }

    public void setAddressId(BigDecimal addressId) {
        this.addressId = addressId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getIdentify() {
        return identify;
    }

    public void setIdentify(String identify) {
        this.identify = identify;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getIdentifyRoleType() {
        return identifyRoleType;
    }

    public void setIdentifyRoleType(Integer identifyRoleType) {
        this.identifyRoleType = identifyRoleType;
    }

    public String getIdentityCard() {
        return identityCard;
    }

    public void setIdentityCard(String identityCard) {
        this.identityCard = identityCard;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPartner() {
        return partner;
    }

    public void setPartner(BigDecimal partner) {
        this.partner = partner;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    public Integer getProfileLevel() {
        return profileLevel;
    }

    public void setProfileLevel(Integer profileLevel) {
        this.profileLevel = profileLevel;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getShip() {
        return ship;
    }

    public void setShip(Integer ship) {
        this.ship = ship;
    }

    public Integer getShop() {
        return shop;
    }

    public void setShop(Integer shop) {
        this.shop = shop;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public Integer getUserGroupId() {
        return userGroupId;
    }

    public void setUserGroupId(Integer userGroupId) {
        this.userGroupId = userGroupId;
    }

    public Timestamp getUtimestamp() {
        return utimestamp;
    }

    public void setUtimestamp(Timestamp utimestamp) {
        this.utimestamp = utimestamp;
    }

    public String getVaId() {
        return vaId;
    }

    public void setVaId(String vaId) {
        this.vaId = vaId;
    }

    public String getVaToken() {
        return vaToken;
    }

    public void setVaToken(String vaToken) {
        this.vaToken = vaToken;
    }

    public BigDecimal getWalletShipId() {
        return walletShipId;
    }

    public void setWalletShipId(BigDecimal walletShipId) {
        this.walletShipId = walletShipId;
    }

    public BigDecimal getWalletShopId() {
        return walletShopId;
    }

    public void setWalletShopId(BigDecimal walletShopId) {
        this.walletShopId = walletShopId;
    }
}
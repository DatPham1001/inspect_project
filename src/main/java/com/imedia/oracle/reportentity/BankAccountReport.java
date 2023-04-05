package com.imedia.oracle.reportentity;

import com.imedia.service.userwallet.model.BankAccountDTO;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;


/**
 * The persistent class for the BANK_ACCOUNTS database table.
 */
@Entity
@Table(name = "BANK_ACCOUNTS")
@SqlResultSetMapping(
        name = "BankAccountsMapping",
        classes = {
                @ConstructorResult(
                        targetClass = BankAccountDTO.class,
                        columns = {
                                @ColumnResult(name = "id", type = BigDecimal.class),
                                @ColumnResult(name = "bank_account", type = String.class),
                                @ColumnResult(name = "bank_account_name", type = String.class),
                                @ColumnResult(name = "phone", type = String.class),
                                @ColumnResult(name = "role_type", type = BigDecimal.class),
                                @ColumnResult(name = "status", type = Integer.class),
                                @ColumnResult(name = "type", type = Integer.class),
                                @ColumnResult(name = "withdraw_type", type = Integer.class),
                                @ColumnResult(name = "bank_code", type = String.class),
                                @ColumnResult(name = "bankName", type = String.class),
                                @ColumnResult(name = "bankShortName", type = String.class),
                        }
                )
        }
)
public class BankAccountReport implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
//    @SequenceGenerator(name = "ISEQ$$_74127", sequenceName = "ISEQ$$_74127", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", insertable = false)
    private BigDecimal id;
    @Column(name = "APP_USER_ID")
    private Long appUserId;

    @Column(name = "BANK_ACCOUNT")
    private String bankAccount;

    @Column(name = "BANK_ACCOUNT_NAME")
    private String bankAccountName;

    @Column(name = "BANK_ADDRESS")
    private String bankAddress;

    @Column(name = "BANK_BRANCH_ID")
    private BigDecimal bankBranchId;

    @Column(name = "BANK_ID")
    private BigDecimal bankId;

    private String email;

    private String phone;

    @Column(name = "ROLE_TYPE")
    private BigDecimal roleType;

    private Integer status;

    @Column(name = "TYPE")
    private Integer type;

    @UpdateTimestamp
    @Column(name = "UTIMESTAMP")
    private Timestamp utimestamp;


    @Column(name = "WITHDRAW_TYPE")
    private Integer withdrawType;

    @Column(name = "BANK_CODE")
    private String bankCode;

    @Column(name = "IS_DELETED")
    private Integer isDeleted;

    public BankAccountReport() {
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public Integer getWithdrawType() {
        return withdrawType;
    }

    public void setWithdrawType(Integer withdrawType) {
        this.withdrawType = withdrawType;
    }

    public Long getAppUserId() {
        return this.appUserId;
    }

    public void setAppUserId(Long appUserId) {
        this.appUserId = appUserId;
    }

    public String getBankAccount() {
        return this.bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankAccountName() {
        return this.bankAccountName;
    }

    public void setBankAccountName(String bankAccountName) {
        this.bankAccountName = bankAccountName;
    }

    public String getBankAddress() {
        return this.bankAddress;
    }

    public void setBankAddress(String bankAddress) {
        this.bankAddress = bankAddress;
    }

    public BigDecimal getBankBranchId() {
        return this.bankBranchId;
    }

    public void setBankBranchId(BigDecimal bankBranchId) {
        this.bankBranchId = bankBranchId;
    }

    public BigDecimal getBankId() {
        return this.bankId;
    }

    public void setBankId(BigDecimal bankId) {
        this.bankId = bankId;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BigDecimal getId() {
        return this.id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public BigDecimal getRoleType() {
        return this.roleType;
    }

    public void setRoleType(BigDecimal roleType) {
        this.roleType = roleType;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getType() {
        return this.type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Timestamp getUtimestamp() {
        return this.utimestamp;
    }

    public void setUtimestamp(Timestamp utimestamp) {
        this.utimestamp = utimestamp;
    }
}
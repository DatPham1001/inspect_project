package com.imedia.oracle.repository;

import com.imedia.oracle.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, BigDecimal> {
//    @Transactional
//    @Modifying
//    @Query(value = "UPDATE BANK_ACCOUNTS SET IS_DELETED = 0 " +
//            "WHERE PHONE = :phone AND BANK_ACCOUNT = :bankAccount)", nativeQuery = true)
//    void deleteBankAccount(@Param("phone") String phone, @Param("bankAccount") String bankAccount);

    @Transactional
    @Modifying
    @Query(value = "UPDATE BANK_ACCOUNTS SET IS_DELETED = 0 " +
            "WHERE ID = :id)", nativeQuery = true)
    void deleteBankAccount(@Param("id") BigDecimal id);

    List<BankAccount> findAllByBankAccountAndAppUserIdAndIsDeleted(String bankAccount, Long appUserId, Integer isDeleted);

    BankAccount findByIdAndPhone(BigDecimal id, String phone);

    BankAccount findByIdAndPhoneAndIsDeleted(BigDecimal id, String phone, Integer isDeleted);

    List<BankAccount>  findAllByBankAccountAndAppUserIdAndBankCodeAndIsDeleted(String bankAccount, Long appUserId, String bankCode, Integer isDeleted);
}


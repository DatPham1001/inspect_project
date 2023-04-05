package com.imedia.oracle.repository;

import com.imedia.oracle.entity.VaWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface VaWalletRepository extends JpaRepository<VaWallet, Long> {
    @Transactional
    @Modifying
    @Query(value = " INSERT INTO VA_WALLETS (USER_ID,SHOP_CODE,USER_NAME,BANK_CODE,BANK_NAME,UTIMESTAMP) " +
            "VALUES (:userId,:shopCode,:username,:bankCode,:bankName,sysdate)", nativeQuery = true)
    void insertVaWallet(@Param("userId") Long userId, @Param("shopCode") String shopCode,
                        @Param("username") String username, @Param("bankCode") String bankCode,
                        @Param("bankName") String bankName);

    VaWallet findByUserId(Long userId);

    VaWallet findByShopCode(String shopCode);
}

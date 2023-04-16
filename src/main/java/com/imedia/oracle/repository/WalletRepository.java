package com.imedia.oracle.repository;

import com.imedia.oracle.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    @Modifying
    @Transactional
    @Query(value = "insert into wallets (account_epurse_id,user_id) values (:account_epurse_id,:user_id)", nativeQuery = true)
    void insertWallet(@Param("account_epurse_id") Long account_epurse_id,
                      @Param("user_id") Long user_id);


    Wallet findByUserId(BigDecimal userId);
}

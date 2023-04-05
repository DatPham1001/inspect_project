package com.imedia.oracle.repository;

import com.imedia.oracle.entity.WalletLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface WalletLogRepository extends JpaRepository<WalletLog, Long> {
    @Query(value = "Select * from WALLET_LOGS\n" +
//            "where  (sysdate - 5/24/60 > UTIMESTAMP)\n" +
            "where  is_deleted = 1\n" +
            "and (SYSTIMESTAMP - UTIMESTAMP) > (INTERVAL '1' SECOND) * 300 " +
            "and status = 2 " +
            "and type IN (11,12,1) "
            , nativeQuery = true)
    List<WalletLog> getPendingLog();


    WalletLog findByCode(String code);

    @Query(value = "SELECT * FROM WALLET_LOGS WHERE REASON like '%Cộng tiền vào%'\n" +
            "OR REASON LIKE '%Rút tiền vào%'", nativeQuery = true)
    List<WalletLog> getW();

    List<WalletLog> findByOrderDetailIdAndTypeAndIsDeleted(BigDecimal orderDetail, BigDecimal type, int isDeleted);

}

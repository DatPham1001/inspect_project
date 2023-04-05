package com.imedia.oracle.repository;

import com.imedia.oracle.entity.HistoryChangeInfoReceiver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface HistoryChangeReceiverRepository extends JpaRepository<HistoryChangeInfoReceiver, Long> {
    @Query(value = " SELECT COUNT(ID) FROM HISTORY_CHANGE_INFO_RECEIVER WHERE CHANGE_TYPE = :type AND ORDER_DETAIL_CODE =:code", nativeQuery = true)
    int countUpdatedChange(@Param("type") String type,
                           @Param("code") BigDecimal orderDetailCode);
}

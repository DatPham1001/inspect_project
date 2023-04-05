package com.imedia.oracle.repository;

import com.imedia.oracle.entity.SvcOrderLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SvcOrderLogRepository extends JpaRepository<SvcOrderLog, Long> {
    List<SvcOrderLog> findAllBySvcOrderDetailCodeOrderByCreatedDateDesc(BigDecimal orderDetailCode);

    List<SvcOrderLog> findAllBySvcOrderDetailCodeAndToStatus(BigDecimal orderDetailCode,Integer toStatus);
    @Query(value = "SELECT * FROM SVC_ORDER_LOG WHERE SVC_ORDER_DETAIL_CODE = :code AND  TO_STATUS = :status", nativeQuery = true)
    List<SvcOrderLog> getValidUpdateOrder(@Param("code") BigDecimal orderDetailCode,
                                          @Param("status") Integer status);
}

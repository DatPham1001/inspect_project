package com.imedia.oracle.reportrepository;

import com.imedia.oracle.reportentity.OrderLogReport;
import com.imedia.oracle.reportentity.SvcOrderLogReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderLogReportRepository extends JpaRepository<OrderLogReport,Long> {
    List<OrderLogReport> findAllByOrderDetailIdOrderByCreateAtDesc(Long orderDetailId);
    List<OrderLogReport> findAllByOrderDetailIdAndToStatusOrderByIdDesc(Long orderDetailId,Integer status);

    @Query(value = "SELECT * FROM ORDER_LOGS WHERE ORDER_DETAIL_ID = :code ORDER BY CREATE_AT DESC OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY"
            , nativeQuery = true)
    OrderLogReport getLastNote(@Param("code") Long code);
}

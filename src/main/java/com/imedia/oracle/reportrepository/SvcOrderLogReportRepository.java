package com.imedia.oracle.reportrepository;

import com.imedia.oracle.reportentity.SvcOrderLogReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SvcOrderLogReportRepository extends JpaRepository<SvcOrderLogReport, Long> {
    @Query(value = "SELECT * FROM SVC_ORDER_LOG \n" +
            "WHERE SVC_ORDER_DETAIL_CODE  = :code " +
            "ORDER BY ID DESC,CREATED_DATE DESC OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY", nativeQuery = true)
    SvcOrderLogReport getLastLog(@Param("code") BigDecimal code);

    List<SvcOrderLogReport> findAllBySvcOrderDetailCodeOrderByIdDesc(BigDecimal orderDetailCode);
}

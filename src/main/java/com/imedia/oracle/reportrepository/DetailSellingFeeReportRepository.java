package com.imedia.oracle.reportrepository;

import com.imedia.oracle.entity.DetailSellingFee;
import com.imedia.oracle.reportentity.DetailSellingFeeReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface DetailSellingFeeReportRepository extends JpaRepository<DetailSellingFeeReport, Long> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM DETAIL_SELLING_FEE WHERE ORDER_DETAIL_CODE = :code", nativeQuery = true)
    void deleteSellingFeeByDetailCode(@Param("code") BigDecimal code);

    List<DetailSellingFeeReport> findAllByOrderDetailCode(BigDecimal code);

}

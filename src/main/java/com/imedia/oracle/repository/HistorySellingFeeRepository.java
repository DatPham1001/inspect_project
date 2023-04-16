package com.imedia.oracle.repository;

import com.imedia.oracle.entity.HistoryDetailSellingFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface HistorySellingFeeRepository extends JpaRepository<HistoryDetailSellingFee, Long> {

    List<HistoryDetailSellingFee> findAllByOrderDetailCodeAndCode(BigDecimal orderCode, String code);
}

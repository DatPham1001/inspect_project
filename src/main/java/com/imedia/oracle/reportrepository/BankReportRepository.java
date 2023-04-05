package com.imedia.oracle.reportrepository;

import com.imedia.oracle.reportentity.BankReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankReportRepository extends JpaRepository<BankReport, Long> {

    @Query(value = "SELECT * FROM BANKS WHERE REPLACE(LOWER(NAME), ' ', '') LIKE LOWER(CONCAT(CONCAT('%', :searchString), '%'))" +
            "OR REPLACE(LOWER(CODE), ' ', '') LIKE LOWER(CONCAT(CONCAT('%', :searchString), '%'))",nativeQuery = true)
    List<BankReport> getBankList (@Param("searchString") String searchString);
}

package com.imedia.oracle.reportrepository;

import com.imedia.oracle.reportentity.AppContractReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppContractReportRepository extends JpaRepository<AppContractReport, Long> {
    AppContractReport findByAppUserId(Long appUserId);

}

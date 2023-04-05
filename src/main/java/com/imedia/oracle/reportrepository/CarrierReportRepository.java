package com.imedia.oracle.reportrepository;

import com.imedia.oracle.entity.Carrier;
import com.imedia.oracle.reportentity.CarrierReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarrierReportRepository extends JpaRepository<CarrierReport, Long> {
    CarrierReport findCarrierById(Long id);
}

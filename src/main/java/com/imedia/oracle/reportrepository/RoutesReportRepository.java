package com.imedia.oracle.reportrepository;

import com.imedia.oracle.reportentity.RoutesReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoutesReportRepository extends JpaRepository<RoutesReport, Long> {
	@Query(value = "SELECT * FROM ROUTES \n" +
            "WHERE STATUS = 1 AND STORE_ID = :storages ORDER BY ID ASC", nativeQuery = true)
	List<RoutesReport> filterRouter(@Param("storages") Integer storages);
}

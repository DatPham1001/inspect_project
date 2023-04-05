package com.imedia.oracle.reportrepository;

import com.imedia.oracle.reportentity.RouteAreaReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface RouteAreaReportRepository extends JpaRepository<RouteAreaReport, Long> {
    @Query(value = "SELECT DISTINCT r.STORE_ID FROM ROUTE_AREAS ra LEFT JOIN ROUTES r ON r.ID = ra.ROUTE_ID " +
            "WHERE ra.IS_DELETED = 1 AND ra.AREA_CODE = :wardCode", nativeQuery = true)
    List<BigDecimal> getStoreIdToCheck(@Param("wardCode") String wardCode);

    @Query(value = "SELECT DISTINCT r.ID FROM ROUTE_AREAS ra LEFT JOIN ROUTES r ON r.ID = ra.ROUTE_ID " +
            "WHERE ra.IS_DELETED = 1 AND ra.AREA_CODE = :wardCode", nativeQuery = true)
    List<BigDecimal> getRouteIdToCheck(@Param("wardCode") String wardCode);
}

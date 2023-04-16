package com.imedia.oracle.reportrepository;

import com.imedia.oracle.reportentity.V2PackageReport;
import com.imedia.service.order.dto.PackageOM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface V2PackageReportRepository extends JpaRepository<V2PackageReport, Long> {
    @Query(value = "SELECT ID,CODE,NAME FROM(SELECT DISTINCT(SERVICE_PACK_ID) as PACK_ID \n" +
            "FROM SVC_ORDER_DETAILS \n" +
            "WHERE SHOP_ID = (SELECT ID FROM APP_USERS WHERE PHONE = :phone )) s\n" +
            "LEFT JOIN V2_PACKAGES p ON p.Id = s.PACK_ID", nativeQuery = true)
    List<PackageOM> getUsedPackages(@Param("phone") String phone);
}

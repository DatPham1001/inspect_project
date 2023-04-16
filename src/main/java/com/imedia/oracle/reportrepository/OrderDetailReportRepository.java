package com.imedia.oracle.reportrepository;

import com.imedia.oracle.reportentity.OrderDetailReport;
import com.imedia.service.order.dto.ConsigneeInfoOM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderDetailReportRepository extends JpaRepository<OrderDetailReport, Long> {

    @Query(value = "SELECT DISTINCT CONSIGNEE,PHONE, COUNT(PHONE) as BUYCOUNT\n" +
            "FROM SVC_ORDER_DETAILS \n" +
            "WHERE (PHONE LIKE CONCAT(:search,'%') " +
            "OR CONSIGNEE LIKE CONCAT( CONCAT('%',:search),'%')) " +
            "AND SHOP_ID = (SELECT ID FROM APP_USERS WHERE PHONE = :username )\n" +
            "GROUP BY CONSIGNEE,PHONE ORDER BY BUYCOUNT DESC FETCH NEXT 5 ROW ONLY", nativeQuery = true)
    List<ConsigneeInfoOM> getBoughtUser(@Param("search") String search, @Param("username") String username);

    OrderDetailReport findByOrderDetailCode(BigDecimal orderDetailCode);
}

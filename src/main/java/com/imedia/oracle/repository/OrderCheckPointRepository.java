package com.imedia.oracle.repository;

import com.imedia.oracle.entity.OrderCheckPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface OrderCheckPointRepository extends JpaRepository<OrderCheckPoint, Long> {
    @Query(value = " SELECT oc.* FROM ORDER_CHECK_POINTS oc " +
            "LEFT JOIN ORDER_DETAILS od ON od.ORDER_CHECK_POINT_ID = oc.ID " +
            "WHERE od.ORDER_DETAIL_CODE = :code ", nativeQuery = true)
    OrderCheckPoint getCheckPointyOrderCode(@Param("code") BigDecimal orderDetailCode);
}

package com.imedia.oracle.repository;

import com.imedia.oracle.entity.OrderPartialRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderPartialRequestRepository extends JpaRepository<OrderPartialRequest, Long> {
    OrderPartialRequest findByOrderDetailCodeAndIsConfirmed(BigDecimal orderDetailCode, int isConfirmed);

    @Query(value = "SELECT o.* FROM ORDER_PARTIAL_REQUEST o \n" +
            "LEFT JOIN SVC_ORDER_DETAILS d ON d.SVC_ORDER_DETAIL_CODE = o.ORDER_DETAIL_CODE \n" +
            "LEFT JOIN APP_USERS a ON a.ID = d.SHOP_ID WHERE o.ID = :id AND a.PHONE = :phone " +
            "AND o.IS_CONFIRMED = 0", nativeQuery = true)
    OrderPartialRequest getConfirmPartialRequest(@Param("id") Long id, @Param("phone") String phone);

    @Query(value = "SELECT p.* FROM ORDER_PARTIAL_REQUEST p \n" +
            "INNER JOIN SVC_ORDER_DETAILS d ON d.SVC_ORDER_DETAIL_CODE = p.ORDER_DETAIL_CODE \n" +
            "WHERE (SYSTIMESTAMP - UPDATE_DATE) > ((INTERVAL '1' SECOND) * :second) \n" +
            "AND IS_CONFIRMED = 0 ", nativeQuery = true)
    List<OrderPartialRequest> getOverWaitRequest(@Param("second") String second);
}

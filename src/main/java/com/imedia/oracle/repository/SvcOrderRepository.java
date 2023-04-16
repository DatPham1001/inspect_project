package com.imedia.oracle.repository;

import com.imedia.oracle.entity.SvcOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;

@Repository
public interface SvcOrderRepository extends JpaRepository<SvcOrder, Long> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM SVC_ORDERS WHERE ORDER_CODE = :code", nativeQuery = true)
    void deleteOrderByCode(@Param("code") BigDecimal code);

    @Modifying
    @Transactional
    @Query(value = "UPDATE SVC_ORDERS SET STATUS = :status WHERE ORDER_CODE = :code ", nativeQuery = true)
    void updateStatus(@Param("status") Integer status, @Param("code") BigDecimal code);

    @Modifying
    @Transactional
    @Query(value = "UPDATE SVC_ORDERS SET STATUS = :status,EXCEPT_SHIP_ID = :shipId WHERE ORDER_CODE = :code ", nativeQuery = true)
    void updateStatusAndShipId(@Param("status") Integer status,
                               @Param("shipId") Long shipId,
                               @Param("code") BigDecimal code);


    SvcOrder findByOrderCode(BigDecimal orderCode);

    SvcOrder findByOrderCodeAndShopId(BigDecimal orderCode, BigDecimal shopId);
    SvcOrder findByOrderCodeAndShopIdAndStatus(BigDecimal orderCode, BigDecimal shopId,int status);
}

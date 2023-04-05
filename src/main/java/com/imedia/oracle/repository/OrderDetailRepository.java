package com.imedia.oracle.repository;

import com.imedia.oracle.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    @Transactional
    @Modifying
    @Query(value = " UPDATE ORDER_DETAILS SET CONSIGNEE = :name,PHONE = :phone WHERE ORDER_DETAIL_CODE = :code", nativeQuery = true)
    void updateShipOrderDetail(@Param("name") String name, @Param("phone") String phone, @Param("code") BigDecimal code);

    @Transactional
    @Modifying
    @Query(value = " UPDATE ORDER_DETAILS d SET d.WEIGHT = :weight,d.WIDTH = :width,d.HEIGHT = :height,d.LENGTH = :length " +
            " WHERE ORDER_DETAIL_CODE = :code", nativeQuery = true)
    void updateShipOrderDetail(@Param("weight") Integer weight,
                               @Param("width") Integer width,
                               @Param("height") Integer height,
                               @Param("length") Integer length,
                               @Param("code") BigDecimal code);

    OrderDetail findByOrderDetailCode(BigDecimal code);

    @Transactional
    @Modifying
    @Query(value = " UPDATE ORDER_DETAILS d SET d.STATUS = :status WHERE ORDER_DETAIL_CODE = :code", nativeQuery = true)
    void updateOrderDetailStatus(@Param("status") Integer status,
                                 @Param("code") BigDecimal code);

    @Transactional
    @Modifying
    @Query(value = "UPDATE ORDER_DETAILS SET COD = :newCod WHERE ORDER_DETAIL_CODE = :code", nativeQuery = true)
    void updateCod(@Param("newCod") BigDecimal newCod, @Param("code") BigDecimal code);
}

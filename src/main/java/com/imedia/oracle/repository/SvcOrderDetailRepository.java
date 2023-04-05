package com.imedia.oracle.repository;

import com.imedia.oracle.entity.SvcOrderDetail;
import com.imedia.service.order.dto.RemainOrderDetailOM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SvcOrderDetailRepository extends JpaRepository<SvcOrderDetail, Long> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM SVC_ORDER_DETAILS WHERE SVC_ORDER_DETAIL_CODE = :code", nativeQuery = true)
    void deleteDetailByCode(@Param("code") BigDecimal code);

    @Query(value = " SELECT * FROM SVC_ORDER_DETAILS WHERE (IS_DELETED IS NULL OR IS_DELETED = 0) AND SVC_ORDER_ID = :orderCode", nativeQuery = true)
    List<SvcOrderDetail> getValidBySvcOrderId(@Param("orderCode")BigDecimal orderCode);

    @Query(value = " SELECT * FROM SVC_ORDER_DETAILS WHERE (IS_DELETED IS NULL OR IS_DELETED = 0) " +
            "AND SVC_ORDER_ID = :orderCode AND STATUS IN ( :statuses )", nativeQuery = true)
    List<SvcOrderDetail> getValidAllBySvcOrderIdAndStatusIn(@Param("orderCode")BigDecimal orderCode,
                                                        @Param("statuses") List<Integer> statuses);

    @Query(value = " SELECT * FROM SVC_ORDER_DETAILS WHERE (IS_DELETED IS NULL OR IS_DELETED = 0) AND SVC_ORDER_DETAIL_CODE = :code", nativeQuery = true)
    SvcOrderDetail getValidBySvcOrderDetailCode(@Param("code") BigDecimal code);

    @Query(value = " SELECT * FROM SVC_ORDER_DETAILS WHERE (IS_DELETED IS NULL OR IS_DELETED = 0)" +
            " AND SVC_ORDER_DETAIL_CODE = :code AND SHOP_ID = :appUserId AND STATUS = :status", nativeQuery = true)
    SvcOrderDetail getValidSvcOrderDetailCodeAndShopIdAndStatus(@Param("code")BigDecimal code,
                                                              @Param("appUserId")Long appUserId,
                                                              @Param("status")Integer status);


    @Modifying
    @Transactional
    @Query(value = "UPDATE  SVC_ORDER_DETAILS SET STATUS = :status WHERE SVC_ORDER_DETAIL_CODE = :code ", nativeQuery = true)
    void updateStatus(@Param("status") Integer status, @Param("code") BigDecimal code);


    @Query(value = "SELECT * from SVC_ORDER_DETAILS WHERE SVC_ORDER_DETAIL_CODE = :code  " +
            "AND SHOP_ID = (SELECT ID FROM APP_USERS WHERE PHONE = :phone ) AND (IS_DELETED IS NULL OR IS_DELETED = 0)", nativeQuery = true)
    SvcOrderDetail getOrderDetailByCodeWithPhone(@Param("code") BigDecimal code,
                                                 @Param("phone") String phone);

    @Query(value = "SELECT * from SVC_ORDER_DETAILS WHERE SVC_ORDER_DETAIL_CODE IN(:code)  " +
            "AND SHOP_ID = (SELECT ID FROM APP_USERS WHERE PHONE = :phone ) AND (IS_DELETED IS NULL OR IS_DELETED = 0)", nativeQuery = true)
    List<SvcOrderDetail> getAllOrderDetailByCodeWithPhone(@Param("code") List<BigDecimal> code,
                                                          @Param("phone") String phone);

    @Query(value = "SELECT * from SVC_ORDER_DETAILS WHERE SVC_ORDER_ID IN(:code)" +
            "AND SHOP_ID = (SELECT ID FROM APP_USERS WHERE PHONE = :phone ) AND (IS_DELETED IS NULL OR IS_DELETED = 0)", nativeQuery = true)
    List<SvcOrderDetail> getAllOrderDetailByOrderCodeWithPhone(@Param("code") String code,
                                                               @Param("phone") String phone);

    @Query(value = "SELECT * from SVC_ORDER_DETAILS WHERE SVC_ORDER_ID IN(:code) " +
            "AND SHOP_ID = (SELECT ID FROM APP_USERS WHERE PHONE = :phone ) AND (IS_DELETED IS NULL OR IS_DELETED = 0)", nativeQuery = true)
    List<SvcOrderDetail> getAllOrderDetailByOrderCodesWithPhone(@Param("code") List<BigDecimal> code,
                                                                @Param("phone") String phone);

    @Query(value = "SELECT * from SVC_ORDER_DETAILS WHERE SHOP_ID = (SELECT ID FROM APP_USERS WHERE PHONE = :phone ) " +
            "AND STATUS IN ( :groupStatus ) AND (IS_DELETED IS NULL OR IS_DELETED = 0) ", nativeQuery = true)
    List<SvcOrderDetail> getAllOrderDetailByPhoneAndGroupStatus(@Param("phone") String phone,
                                                                @Param("groupStatus") List<Integer> groupStatus);

    @Query(value = "SELECT * from SVC_ORDER_DETAILS " +
            "WHERE SHOP_ID = (SELECT ID FROM APP_USERS WHERE PHONE = :phone ) " +
            "AND SVC_ORDER_ID IN (:code) " +
            "AND STATUS IN ( :groupStatus )" +
            "AND (IS_DELETED IS NULL OR IS_DELETED = 0) ", nativeQuery = true)
    List<SvcOrderDetail> getAllOrderDetailByCodesPhoneAndGroupStatus(@Param("phone") String phone,
                                                                     @Param("code") List<BigDecimal> code,
                                                                     @Param("groupStatus") List<Integer> groupStatus);

    @Query(value = "SELECT o.ORDER_CODE as orderCode,COUNT(d.ID) as remain FROM SVC_ORDER_DETAILS d " +
            "LEFT JOIN SVC_ORDERS o ON o.ORDER_CODE = d.SVC_ORDER_ID " +
            "WHERE o.ORDER_CODE = :code " +
            "AND d.STATUS IN ( :status ) " +
            "AND (IS_DELETED IS NULL OR IS_DELETED = 0)" +
            " GROUP BY o.ORDER_CODE", nativeQuery = true)
    RemainOrderDetailOM getRemainOrderDetail(@Param("code") BigDecimal orderCode,
                                             @Param("status") List<Integer> status);

    @Query(value = "SELECT * FROM SVC_ORDER_DETAILS " +
            "WHERE SVC_ORDER_ID = :orderCode " +
            "AND SVC_ORDER_DETAIL_CODE NOT IN ( :canceledCode ) AND (IS_DELETED IS NULL OR IS_DELETED = 0) " +
            "AND STATUS NOT IN (501,107,924,903,504,2001, 2006,20062,20063,20061,2007) ", nativeQuery = true)
    List<SvcOrderDetail> getOtherDetailToCalculate(@Param("canceledCode") BigDecimal canceledOrderDetail,
                                                   @Param("orderCode") BigDecimal orderCode);

}

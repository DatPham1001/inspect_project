package com.imedia.oracle.repository;

import com.imedia.oracle.entity.DetailSellingFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface DetailSellingFeeRepository extends JpaRepository<DetailSellingFee, Long> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM DETAIL_SELLING_FEE WHERE ORDER_DETAIL_CODE = :code", nativeQuery = true)
    void deleteSellingFeeByDetailCode(@Param("code") BigDecimal code);

    List<DetailSellingFee> findAllByOrderDetailCode(BigDecimal code);

    DetailSellingFee findByCodeAndOrderDetailCode(String code, BigDecimal detailCode);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM DETAIL_SELLING_FEE WHERE ORDER_DETAIL_CODE = :code AND CODE = :feeCode", nativeQuery = true)
    void deleteSellingFeeByDetailCode(@Param("code") BigDecimal code, @Param("feeCode") String feeCode);

    @Modifying
    @Transactional
    @Query(value = "UPDATE DETAIL_SELLING_FEE SET VALUE = VALUE_OLD,VALUE_OLD = VALUE " +
            "WHERE ORDER_DETAIL_CODE = :code AND CODE = 'UPDATE_FEE'", nativeQuery = true)
    void revertUpdateFee(@Param("code") BigDecimal orderCode);

    @Query(value = "SELECT SUM(VALUE) FROM DETAIL_SELLING_FEE " +
            "WHERE ORDER_DETAIL_CODE = :code ", nativeQuery = true)
    BigDecimal sumTotalFee(@Param("code") BigDecimal code);

    List<DetailSellingFee> findAllByCodeAndOrderDetailCode(String code, BigDecimal detailCode);
}

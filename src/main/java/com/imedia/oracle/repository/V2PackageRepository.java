package com.imedia.oracle.repository;

import com.imedia.oracle.entity.V2Package;
import com.imedia.service.postage.model.FeeSettingInfoOM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface V2PackageRepository extends JpaRepository<V2Package, BigDecimal> {
    V2Package findByCode(String code);

    V2Package findV2PackageById(BigDecimal id);

    List<V2Package> findAllByStatusAndPriceSettingType(BigDecimal status, int type);

    List<V2Package> findAllByStatusAndPriceSettingTypeAndCodeIn(BigDecimal status, int type, List<String> codes);

    @Query(value = " SELECT pf.Fee_SETTING_ID as feeSettingId,fs.NAME,fs.TYPE,fb.MIN,fb.MAX FROM V2_PACKAGE_FEE pf\n" +
            "LEFT JOIN V2_FEE_SETTING fs ON pf.FEE_SETTING_ID = fs.ID\n" +
            "LEFT JOIN V2_FEE_BASE fb ON fb.FEE_SETTING_ID = fs.ID\n" +
            "WHERE pf.PACKAGE_ID = :packageId " +
            "AND fs.TYPE = :type " +
            "AND fb.TYPE_CHANGE = :changeType", nativeQuery = true)
    FeeSettingInfoOM getConfigFeeSetting(@Param("packageId") int packageId,
                                         @Param("type") int type, @Param("changeType") int changeType);

    @Query(value = " SELECT pf.Fee_SETTING_ID as feeSettingId,fs.NAME,fs.TYPE,fb.MIN,fb.MAX FROM V2_PACKAGE_FEE pf\n" +
            "LEFT JOIN V2_FEE_SETTING fs ON pf.FEE_SETTING_ID = fs.ID\n" +
            "LEFT JOIN V2_FEE_BASE fb ON fb.FEE_SETTING_ID = fs.ID\n" +
            "WHERE pf.PACKAGE_ID = :packageId " +
            "AND fs.TYPE = 5 ", nativeQuery = true)
    FeeSettingInfoOM getRangeCodChange(@Param("packageId") int packageId);

    @Query(value = "select * from v2_packages where status = 1 and price_setting_type =1\n" +
            "order by id desc", nativeQuery = true)
    List<V2Package> getValidPackages();
}

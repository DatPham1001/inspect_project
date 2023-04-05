package com.imedia.oracle.repository;

import com.imedia.oracle.entity.Ward;
import com.imedia.service.pickupaddress.model.AddressDataOM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WardRepository extends JpaRepository<Ward, Long> {
    Ward findByCode(String code);

    List<Ward> findAllByDistrictCode(String code);

    @Query(value = "select distinct PROVINCE_CODE from WARDS ", nativeQuery = true)
    List<String> selectProvinceCodes();

    @Query(value = "SELECT w.ID  as wardId,d.ID as districtId,p.Id as provinceId " +
            "FROM WARDS w " +
            "LEFT JOIN DISTRICTS d ON w.DISTRICT_CODE = d.CODE " +
            "LEFT JOIN PROVINCES p ON w.PROVINCE_CODE = p.CODE " +
            "WHERE w.CODE = :wardCode", nativeQuery = true)
    AddressDataOM getAddressDataId(@Param("wardCode") String wardCode);
}

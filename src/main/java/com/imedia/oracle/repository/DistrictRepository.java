package com.imedia.oracle.repository;

import com.imedia.oracle.entity.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistrictRepository extends JpaRepository<District, Long> {
    District findByCode(String code);

    @Query(value = "SELECT * FROM \"DISTRICTS\" where PROVINCE_CODE = :code ", nativeQuery = true)
    List<District> getDistrict(@Param("code") String provinceCode);
}

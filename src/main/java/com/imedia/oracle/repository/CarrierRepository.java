package com.imedia.oracle.repository;

import com.imedia.oracle.entity.Carrier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CarrierRepository extends JpaRepository<Carrier, Long> {
    Carrier findCarrierById(Long id);


    @Query(value = "SELECT * FROM CARRIERS WHERE CODE = 'HLS' OR CODE = 'HOLA'", nativeQuery = true)
    Carrier getHolaCarrier();

    Carrier findByCode(String code);
}

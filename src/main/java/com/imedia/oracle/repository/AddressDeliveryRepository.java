package com.imedia.oracle.repository;

import com.imedia.oracle.entity.AddressDelivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressDeliveryRepository extends JpaRepository<AddressDelivery, Long> {
    @Query(value = "SELECT * FROM ADDRESS_DELIVERY \n" +
            "WHERE  LOWER( STRING_FILTER ) = LOWER(:filter)\n" +
            "OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY  ", nativeQuery = true)
    AddressDelivery stringFilterAddress(@Param("filter") String filter);

    AddressDelivery findAddressDeliveryById(Long id);
}

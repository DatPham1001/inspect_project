package com.imedia.oracle.repository;

import com.imedia.oracle.entity.V2PriceSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface V2PriceSettingRepository extends JpaRepository<V2PriceSetting, Long> {
}

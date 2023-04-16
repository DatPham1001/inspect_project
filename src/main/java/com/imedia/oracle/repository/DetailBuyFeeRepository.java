package com.imedia.oracle.repository;

import com.imedia.oracle.entity.DetailBuyingFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetailBuyFeeRepository extends JpaRepository<DetailBuyingFee, Long> {

}

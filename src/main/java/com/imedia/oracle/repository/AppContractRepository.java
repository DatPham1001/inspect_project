package com.imedia.oracle.repository;

import com.imedia.oracle.entity.AppContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppContractRepository extends JpaRepository<AppContract, Long> {
    AppContract findByAppUserId(Long appUserId);
}

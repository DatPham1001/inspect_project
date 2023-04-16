package com.imedia.oracle.repository;

import com.imedia.oracle.entity.ShipperProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipperProfileRepository extends JpaRepository<ShipperProfile, Long> {
    ShipperProfile findByProfileId(Long appUserId);
}

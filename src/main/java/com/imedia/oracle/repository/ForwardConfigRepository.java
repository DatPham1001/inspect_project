package com.imedia.oracle.repository;

import com.imedia.oracle.entity.LeadtimeForwardConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ForwardConfigRepository extends JpaRepository<LeadtimeForwardConfig, Long> {
    List<LeadtimeForwardConfig> findAllByPriceSettingIdAndIsDeleteOrderByRankAsc(int id, int isDelete);

    List<LeadtimeForwardConfig> findAllByPriceSettingIdAndIsDeleteAndPackIdOrderByRankAsc(int id, int isDelete, int packId);

}

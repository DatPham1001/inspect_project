package com.imedia.oracle.repository;

import com.imedia.oracle.entity.HistoryOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryOrderDetailRepo extends JpaRepository<HistoryOrderDetail,Long> {
}

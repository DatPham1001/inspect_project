package com.imedia.oracle.repository;

import com.imedia.oracle.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatusRepository extends JpaRepository<Status, Integer> {
    List<Status> findAllByStatusOrderByCodeAsc(Integer status);
}

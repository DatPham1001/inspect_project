package com.imedia.oracle.repository;

import com.imedia.oracle.entity.ErrorCodesWebshop;
import org.bouncycastle.util.Integers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ErrorCodeWebshopRepository extends JpaRepository<ErrorCodesWebshop, Integers> {
    List<ErrorCodesWebshop> findAllByType(Integer type);
}

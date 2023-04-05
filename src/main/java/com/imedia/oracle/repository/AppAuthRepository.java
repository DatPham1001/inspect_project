package com.imedia.oracle.repository;

import com.imedia.oracle.entity.AppAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppAuthRepository extends JpaRepository<AppAuth, Long> {
}

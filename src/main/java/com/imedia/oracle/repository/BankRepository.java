package com.imedia.oracle.repository;

import com.imedia.oracle.entity.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankRepository extends JpaRepository<Bank, Long> {
    Bank findByBankCode(String bankCode);
}

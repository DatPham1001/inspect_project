package com.imedia.oracle.reportrepository;

import com.imedia.oracle.reportentity.ProductCategoryReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCategoryReportRepo extends JpaRepository<ProductCategoryReport, Long> {
}

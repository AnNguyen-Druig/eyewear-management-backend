package com.swp391.eyewear_management_backend.repository;

import com.swp391.eyewear_management_backend.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepo extends JpaRepository<Invoice, Long> {
}

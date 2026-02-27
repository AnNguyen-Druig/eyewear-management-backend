package com.swp391.eyewear_management_backend.repository;

import com.swp391.eyewear_management_backend.entity.ContactLens;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactLensRepository extends JpaRepository<ContactLens, Long> {
}

package com.swp391.eyewear_management_backend.repository;

import com.swp391.eyewear_management_backend.entity.ShippingInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShippingInfoRepo extends JpaRepository<ShippingInfo, Long> {
}

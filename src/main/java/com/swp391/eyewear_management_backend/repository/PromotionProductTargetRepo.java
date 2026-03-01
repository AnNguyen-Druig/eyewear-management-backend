package com.swp391.eyewear_management_backend.repository;

import com.swp391.eyewear_management_backend.entity.PromotionProductTarget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PromotionProductTargetRepo extends JpaRepository<PromotionProductTarget, Long> {
    List<PromotionProductTarget> findByPromotion_PromotionIDIn(List<Long> promotionIds);
}
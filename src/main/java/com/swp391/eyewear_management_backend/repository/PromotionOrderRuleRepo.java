package com.swp391.eyewear_management_backend.repository;

import com.swp391.eyewear_management_backend.entity.PromotionOrderRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PromotionOrderRuleRepo extends JpaRepository<PromotionOrderRule, Long> {
    List<PromotionOrderRule> findByPromotion_PromotionIdIn(List<Long> promotionIds);
}
package com.swp391.eyewear_management_backend.service.impl;


import com.swp391.eyewear_management_backend.dto.response.PromotionCandidateResponse;
import com.swp391.eyewear_management_backend.entity.*;
import com.swp391.eyewear_management_backend.repository.PromotionOrderRuleRepo;
import com.swp391.eyewear_management_backend.repository.PromotionProductTargetRepo;
import com.swp391.eyewear_management_backend.repository.PromotionRepo;
import com.swp391.eyewear_management_backend.service.PromotionCalculatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionCalculatorServiceImpl implements PromotionCalculatorService {

    private final PromotionRepo promotionRepo;
    private final PromotionOrderRuleRepo orderRuleRepo;
    private final PromotionProductTargetRepo targetRepo;

    @Override
    public PromotionResult evaluate(List<CartItem> cartItems, BigDecimal subTotal, Long selectedPromotionId) {
        LocalDateTime now = LocalDateTime.now();

        List<Promotion> promos = promotionRepo.findAvailableNow(now);
        if (promos.isEmpty()) {
            return new PromotionResult(BigDecimal.ZERO, null, List.of(), null, Map.of());
        }

        List<Long> promoIds = promos.stream().map(Promotion::getPromotionID).toList();

        Map<Long, PromotionOrderRule> orderRuleMap = orderRuleRepo.findByPromotion_PromotionIDIn(promoIds)
                .stream().collect(Collectors.toMap(r -> r.getPromotion().getPromotionID(), r -> r));

        Map<Long, List<PromotionProductTarget>> targetMap = targetRepo.findByPromotion_PromotionIDIn(promoIds)
                .stream().collect(Collectors.groupingBy(t -> t.getPromotion().getPromotionID()));

        // Build component list (để product-scope tính đúng cho prescription: framePrice/lensPrice)
        List<Component> components = buildComponents(cartItems);

        // Evaluate each promo => estimatedDiscount
        List<PromoEval> evals = new ArrayList<>();
        for (Promotion p : promos) {
            BigDecimal base = eligibleBase(p, subTotal, components, orderRuleMap, targetMap);
            if (base.compareTo(BigDecimal.ZERO) <= 0) continue;

            BigDecimal discount = computeDiscount(p, base);
            if (discount.compareTo(BigDecimal.ZERO) <= 0) continue;

            evals.add(new PromoEval(p, discount, base));
        }

        // availablePromotions list
        List<PromotionCandidateResponse> available = evals.stream()
                .sorted(Comparator.comparing((PromoEval e) -> e.discount).reversed())
                .map(e -> PromotionCandidateResponse.builder()
                        .promotionId(e.p.getPromotionID())
                        .code(e.p.getPromotionCode())
                        .name(e.p.getPromotionName())
                        .scope(e.p.getPromotionScope())
                        .discountType(e.p.getDiscountType())
                        .discountValue(e.p.getDiscountValue())
                        .maxDiscountValue(e.p.getMaxDiscountValue())
                        .estimatedDiscount(e.discount)
                        .endDate(e.p.getEndDate())
                        .description(e.p.getDescription())
                        .build())
                .toList();

        PromotionCandidateResponse recommended = available.isEmpty() ? null : available.get(0);

        // Apply selected promotion if provided; else no discount (UI có thể auto chọn recommended nếu muốn)
        PromoEval applied = null;
        if (selectedPromotionId != null) {
            applied = evals.stream()
                    .filter(e -> e.p.getPromotionID().equals(selectedPromotionId))
                    .findFirst()
                    .orElse(null);
            if (applied == null) {
                // promotion user chọn không hợp lệ cho cart hiện tại
                return new PromotionResult(BigDecimal.ZERO, null, available, recommended, Map.of());
            }
        }

        if (applied == null) {
            return new PromotionResult(BigDecimal.ZERO, null, available, recommended, Map.of());
        }

        // Allocate discount về từng cartItemId để tính prescriptionAmount sau discount
        Map<Long, BigDecimal> itemDiscountMap = allocateDiscountToItems(applied.p, applied.discount, applied.base,
                cartItems, components, orderRuleMap, targetMap);

        return new PromotionResult(applied.discount, applied.p.getPromotionID(), available, recommended, itemDiscountMap);
    }

    /* ====== internal models ====== */
    private record Component(Long cartItemId, Product product, BigDecimal amount) {}
    private record PromoEval(Promotion p, BigDecimal discount, BigDecimal base) {}

    private List<Component> buildComponents(List<CartItem> cartItems) {
        List<Component> list = new ArrayList<>();
        for (CartItem ci : cartItems) {
            int qty = ci.getQuantity() == null || ci.getQuantity() <= 0 ? 1 : ci.getQuantity();

            // Contact lens component
            if (ci.getContactLens() != null && ci.getContactLens().getProduct() != null) {
                BigDecimal unit = firstNonNull(ci.getContactLensPrice(), ci.getPrice());
                list.add(new Component(ci.getCartItemId(), ci.getContactLens().getProduct(), unit.multiply(BigDecimal.valueOf(qty))));
            }

            // Frame component
            if (ci.getFrame() != null && ci.getFrame().getProduct() != null) {
                BigDecimal unit = firstNonNull(ci.getFramePrice(), ci.getPrice());
                list.add(new Component(ci.getCartItemId(), ci.getFrame().getProduct(), unit.multiply(BigDecimal.valueOf(qty))));
            }

            // Lens component
            if (ci.getLens() != null && ci.getLens().getProduct() != null) {
                BigDecimal unit = firstNonNull(ci.getLensPrice(), ci.getPrice());
                list.add(new Component(ci.getCartItemId(), ci.getLens().getProduct(), unit.multiply(BigDecimal.valueOf(qty))));
            }
        }
        return list;
    }

    private BigDecimal eligibleBase(
            Promotion p,
            BigDecimal subTotal,
            List<Component> components,
            Map<Long, PromotionOrderRule> orderRuleMap,
            Map<Long, List<PromotionProductTarget>> targetMap
    ) {
        String scope = p.getPromotionScope(); // ORDER / PRODUCT

        if ("ORDER".equalsIgnoreCase(scope)) {
            PromotionOrderRule rule = orderRuleMap.get(p.getPromotionID());
            if (rule == null) return BigDecimal.ZERO;
            if (subTotal.compareTo(rule.getMinOrderTotal()) < 0) return BigDecimal.ZERO;
            return subTotal;
        }

        if ("PRODUCT".equalsIgnoreCase(scope)) {
            List<PromotionProductTarget> targets = targetMap.getOrDefault(p.getPromotionID(), List.of());
            if (targets.isEmpty()) return BigDecimal.ZERO;

            BigDecimal base = BigDecimal.ZERO;
            for (Component c : components) {
                if (matchesAnyTarget(c.product, targets)) {
                    base = base.add(c.amount);
                }
            }
            return base;
        }

        return BigDecimal.ZERO;
    }

    private boolean matchesAnyTarget(Product product, List<PromotionProductTarget> targets) {
        for (PromotionProductTarget t : targets) {
            String type = t.getTargetType();

            if ("PRODUCT".equalsIgnoreCase(type)) {
                if (t.getProduct() != null && product.getProductID().equals(t.getProduct().getProductID())) return true;
            }
            if ("PRODUCT_TYPE".equalsIgnoreCase(type)) {
                if (t.getProductType() != null && product.getProductType() != null
                        && product.getProductType().getProductTypeID().equals(t.getProductType().getProductTypeID()))
                    return true;
            }
            if ("BRAND".equalsIgnoreCase(type)) {
                if (t.getBrand() != null && product.getBrand() != null
                        && product.getBrand().getBrandID().equals(t.getBrand().getBrandID()))
                    return true;
            }
            if ("BRAND_PRODUCT_TYPE".equalsIgnoreCase(type)) {
                boolean okBrand = t.getBrand() != null && product.getBrand() != null
                        && product.getBrand().getBrandID().equals(t.getBrand().getBrandID());
                boolean okType = t.getProductType() != null && product.getProductType() != null
                        && product.getProductType().getProductTypeID().equals(t.getProductType().getProductTypeID());
                if (okBrand && okType) return true;
            }
        }
        return false;
    }

    private BigDecimal computeDiscount(Promotion p, BigDecimal base) {
        BigDecimal discount;

        if ("PERCENT".equalsIgnoreCase(p.getDiscountType())) {
            discount = base.multiply(p.getDiscountValue())
                    .divide(new BigDecimal("100"), 0, RoundingMode.HALF_UP);

            if (p.getMaxDiscountValue() != null && p.getMaxDiscountValue().compareTo(BigDecimal.ZERO) > 0) {
                discount = discount.min(p.getMaxDiscountValue());
            }
        } else {
            // AMOUNT
            discount = p.getDiscountValue();
        }

        // cannot exceed base
        if (discount.compareTo(base) > 0) discount = base;

        // normalize
        if (discount.compareTo(BigDecimal.ZERO) < 0) discount = BigDecimal.ZERO;
        return discount.setScale(0, RoundingMode.HALF_UP);
    }

    private Map<Long, BigDecimal> allocateDiscountToItems(
            Promotion p,
            BigDecimal discountAmount,
            BigDecimal eligibleBase,
            List<CartItem> cartItems,
            List<Component> components,
            Map<Long, PromotionOrderRule> orderRuleMap,
            Map<Long, List<PromotionProductTarget>> targetMap
    ) {
        Map<Long, BigDecimal> itemDiscount = new HashMap<>();
        if (discountAmount.compareTo(BigDecimal.ZERO) <= 0) return itemDiscount;

        if ("ORDER".equalsIgnoreCase(p.getPromotionScope())) {
            // allocate theo item total
            Map<Long, BigDecimal> itemTotals = new HashMap<>();
            for (CartItem ci : cartItems) {
                int qty = ci.getQuantity() == null || ci.getQuantity() <= 0 ? 1 : ci.getQuantity();
                BigDecimal total = ci.getPrice().multiply(BigDecimal.valueOf(qty));
                itemTotals.put(ci.getCartItemId(), total);
            }
            return proportionalAllocate(itemTotals, discountAmount);
        }

        // PRODUCT scope: allocate theo component eligible amount
        List<PromotionProductTarget> targets = targetMap.getOrDefault(p.getPromotionID(), List.of());

        Map<Long, BigDecimal> eligibleComponentByItem = new HashMap<>();
        for (Component c : components) {
            if (matchesAnyTarget(c.product, targets)) {
                eligibleComponentByItem.merge(c.cartItemId, c.amount, BigDecimal::add);
            }
        }
        return proportionalAllocate(eligibleComponentByItem, discountAmount);
    }

    private Map<Long, BigDecimal> proportionalAllocate(Map<Long, BigDecimal> baseByKey, BigDecimal totalDiscount) {
        BigDecimal baseSum = baseByKey.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        if (baseSum.compareTo(BigDecimal.ZERO) <= 0) return Map.of();

        List<Long> keys = new ArrayList<>(baseByKey.keySet());
        keys.sort(Long::compareTo);

        Map<Long, BigDecimal> out = new HashMap<>();
        BigDecimal allocated = BigDecimal.ZERO;

        for (int i = 0; i < keys.size(); i++) {
            Long k = keys.get(i);
            BigDecimal b = baseByKey.get(k);

            BigDecimal share;
            if (i == keys.size() - 1) {
                share = totalDiscount.subtract(allocated);
            } else {
                share = totalDiscount.multiply(b).divide(baseSum, 0, RoundingMode.HALF_UP);
                allocated = allocated.add(share);
            }
            out.put(k, share);
        }
        return out;
    }

    private BigDecimal firstNonNull(BigDecimal a, BigDecimal b) {
        if (a != null && a.compareTo(BigDecimal.ZERO) > 0) return a;
        return b == null ? BigDecimal.ZERO : b;
    }
}
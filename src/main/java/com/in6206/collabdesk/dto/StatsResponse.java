package com.in6206.collabdesk.dto;

import com.in6206.collabdesk.entity.DealStatus;

import java.math.BigDecimal;
import java.util.Map;

public record StatsResponse(long totalUsers, long totalDeals, Map<DealStatus, Long> dealsByStatus,
                            BigDecimal totalPaidValue, long paymentOverdueDeals) {
}

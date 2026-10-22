package com.in6206.collabdesk.service;

import com.in6206.collabdesk.dto.StatsResponse;
import com.in6206.collabdesk.entity.DealStatus;
import com.in6206.collabdesk.repository.DealRepository;
import com.in6206.collabdesk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService {

    private final DealRepository deals;
    private final UserRepository users;

    public StatsResponse platformStats() {
        Map<DealStatus, Long> byStatus = new EnumMap<>(DealStatus.class);
        for (DealStatus s : DealStatus.values()) {
            byStatus.put(s, deals.countByStatus(s));
        }
        long overdue = deals.findAll().stream().filter(d -> d.isPaymentOverdue()).count();
        BigDecimal paid = deals.sumPaidAmount();
        return new StatsResponse(users.count(), deals.count(), byStatus,
                paid == null ? BigDecimal.ZERO : paid, overdue);
    }
}

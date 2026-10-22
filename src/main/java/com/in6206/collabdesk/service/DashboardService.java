package com.in6206.collabdesk.service;

import com.in6206.collabdesk.entity.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final DealService dealService;
    private final DealActions dealActions;

    @Getter
    @AllArgsConstructor
    public static class Summary {
        private final List<Deal> needsAction;
        private final long activeDeals;
        private final BigDecimal paidTotal;        // creator: earned, brand: spent
        private final BigDecimal awaitingPayment;  // published but not yet paid
        private final long overdueCount;           // overdue deliverables + overdue payments
        private final List<Deliverable> upcoming;  // open deliverables, nearest due date first
    }

    public Summary summaryFor(User user) {
        List<Deal> deals = dealService.listFor(user, null);

        List<Deal> needsAction = deals.stream()
                .filter(d -> dealActions.nextAction(d, user.getRole()) != null)
                .toList();
        long active = deals.stream().filter(d -> !d.getStatus().isClosed()).count();
        BigDecimal paid = sum(deals, DealStatus.PAID);
        BigDecimal awaiting = sum(deals, DealStatus.PUBLISHED);

        List<Deliverable> open = deals.stream()
                .filter(d -> d.getStatus() == DealStatus.ACCEPTED)
                .flatMap(d -> d.getDeliverables().stream())
                .filter(x -> x.getStatus() != DeliverableStatus.APPROVED)
                .sorted(Comparator.comparing(Deliverable::getDueDate))
                .toList();
        long overdue = open.stream().filter(x -> x.getStatus() == DeliverableStatus.OVERDUE).count()
                + deals.stream().filter(Deal::isPaymentOverdue).count();

        return new Summary(needsAction, active, paid, awaiting, overdue, open.stream().limit(5).toList());
    }

    private BigDecimal sum(List<Deal> deals, DealStatus status) {
        return deals.stream()
                .filter(d -> d.getStatus() == status && d.getAgreedAmount() != null)
                .map(Deal::getAgreedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

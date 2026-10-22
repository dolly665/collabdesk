package com.in6206.collabdesk.scheduler;

import com.in6206.collabdesk.entity.Deal;
import com.in6206.collabdesk.entity.DealStatus;
import com.in6206.collabdesk.entity.Deliverable;
import com.in6206.collabdesk.entity.DeliverableStatus;
import com.in6206.collabdesk.repository.DealRepository;
import com.in6206.collabdesk.repository.DeliverableRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;

/**
 * Daily job (P1 + P3): flags late deliverables and unpaid published deals.
 * Can also be triggered manually by an admin for the demo.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OverdueScheduler {

    private final DeliverableRepository deliverables;
    private final DealRepository deals;
    private final Clock clock;

    @Scheduled(cron = "${collabdesk.overdue.cron}")
    @Transactional
    public int runChecks() {
        LocalDate today = LocalDate.now(clock);

        List<Deliverable> late = deliverables.findByStatusInAndDueDateBefore(
                EnumSet.of(DeliverableStatus.PENDING, DeliverableStatus.REVISION_REQUESTED), today);
        late.forEach(d -> d.setStatus(DeliverableStatus.OVERDUE));

        List<Deal> unpaid = deals.findByStatusAndPaymentOverdueFalseAndPaymentDueDateBefore(DealStatus.PUBLISHED, today);
        unpaid.forEach(d -> d.setPaymentOverdue(true));

        log.info("Overdue check: {} deliverable(s) overdue, {} deal(s) with overdue payment", late.size(), unpaid.size());
        return late.size() + unpaid.size();
    }
}

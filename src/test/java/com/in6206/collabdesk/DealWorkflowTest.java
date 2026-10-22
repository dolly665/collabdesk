package com.in6206.collabdesk;

import com.in6206.collabdesk.dto.DealRequests;
import com.in6206.collabdesk.entity.*;
import com.in6206.collabdesk.exception.BusinessRuleException;
import com.in6206.collabdesk.exception.ForbiddenException;
import com.in6206.collabdesk.repository.DealRepository;
import com.in6206.collabdesk.repository.UserRepository;
import com.in6206.collabdesk.scheduler.OverdueScheduler;
import com.in6206.collabdesk.service.DealService;
import com.in6206.collabdesk.service.DeliverableService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests of the business rules against the H2 database and the demo data from DataSeeder.
 * Each test runs in a transaction that is rolled back, so tests don't affect each other.
 */
@SpringBootTest
@Transactional
class DealWorkflowTest {

    @Autowired DealService dealService;
    @Autowired DeliverableService deliverableService;
    @Autowired OverdueScheduler overdueScheduler;
    @Autowired DealRepository deals;
    @Autowired UserRepository users;

    private User user(String username) {
        return users.findByUsername(username).orElseThrow();
    }

    private Deal deal(String title) {
        return deals.findAll().stream().filter(d -> d.getTitle().equals(title)).findFirst().orElseThrow();
    }

    private Deliverable script(Deal d) {
        return d.getDeliverables().stream().filter(x -> x.getType() == DeliverableType.SCRIPT).findFirst().orElseThrow();
    }

    @Test
    void counterOfferThenBrandAcceptsUsesCounterAmount() {
        Deal d = deal("New brown-sugar series launch");          // PROPOSED, Amy, budget 400
        dealService.counter(user("creator_amy"), d.getId(), new DealRequests.Counter(new BigDecimal("520"), "two outlets"));
        Deal accepted = dealService.accept(user("brand_bubbletea"), d.getId());

        assertEquals(DealStatus.ACCEPTED, accepted.getStatus());
        assertEquals(0, new BigDecimal("520").compareTo(accepted.getAgreedAmount()));
        assertEquals(1, accepted.getDeliverables().size(), "a SCRIPT deliverable is created on acceptance");
        assertEquals(DealStatus.COUNTERED, accepted.getHistory().get(accepted.getHistory().size() - 1).getFromStatus());
    }

    @Test
    void creatorCannotOpenSomeoneElsesDeal() {
        Deal amysDeal = deal("New brown-sugar series launch");
        assertThrows(ForbiddenException.class, () -> dealService.getFor(user("creator_ken"), amysDeal.getId()));
    }

    @Test
    void cannotPublishBeforeScriptIsApproved() {
        Deal d = deal("Bubble tea x desk setup crossover");      // ACCEPTED, script SUBMITTED
        BusinessRuleException e = assertThrows(BusinessRuleException.class,
                () -> dealService.publish(user("creator_ken"), d.getId(), "https://youtube.com/x"));
        assertTrue(e.getMessage().contains("approved"));
    }

    @Test
    void publishSetsPaymentDueDate() {
        Deal d = deal("Bubble tea x desk setup crossover");
        deliverableService.approve(user("brand_bubbletea"), script(d).getId());
        Deal published = dealService.publish(user("creator_ken"), d.getId(), "https://youtube.com/x");

        assertEquals(DealStatus.PUBLISHED, published.getStatus());
        assertEquals(published.getPublishedDate().plusDays(published.getPaymentTermDays()), published.getPaymentDueDate());
    }

    @Test
    void revisionLimitIsEnforced() {
        Deal d = deal("Bubble tea x desk setup crossover");      // max 2 rounds, 1 already used
        Long scriptId = script(d).getId();
        User brand = user("brand_bubbletea");

        deliverableService.requestRevision(brand, scriptId, "round 2");
        deliverableService.submit(user("creator_ken"), scriptId, "v3");

        BusinessRuleException e = assertThrows(BusinessRuleException.class,
                () -> deliverableService.requestRevision(brand, scriptId, "round 3"));
        assertTrue(e.getMessage().contains("Revision limit"));
    }

    @Test
    void approvalCanBeUndoneOnlyInsideTheWindow() {
        Deal d = deal("Bubble tea x desk setup crossover");
        Deliverable s = script(d);
        User brand = user("brand_bubbletea");

        deliverableService.approve(brand, s.getId());
        deliverableService.undoApproval(brand, s.getId());
        assertEquals(DeliverableStatus.SUBMITTED, s.getStatus());

        deliverableService.approve(brand, s.getId());
        s.setApprovedAt(LocalDateTime.now().minusDays(1));   // pretend it was approved long ago
        assertThrows(BusinessRuleException.class, () -> deliverableService.undoApproval(brand, s.getId()));
    }

    @Test
    void overdueJobFlagsLateScriptAndLatePayment() {
        int flagged = overdueScheduler.runChecks();

        assertEquals(2, flagged);
        assertTrue(deal("Noise-cancelling earbuds review").isPaymentOverdue());
        assertEquals(DeliverableStatus.OVERDUE, script(deal("Air fryer recipe series")).getStatus());
    }
}

package com.in6206.collabdesk.service;

import com.in6206.collabdesk.dto.DealRequests;
import com.in6206.collabdesk.entity.*;
import com.in6206.collabdesk.exception.BusinessRuleException;
import com.in6206.collabdesk.exception.ForbiddenException;
import com.in6206.collabdesk.exception.NotFoundException;
import com.in6206.collabdesk.repository.BrandProfileRepository;
import com.in6206.collabdesk.repository.CreatorProfileRepository;
import com.in6206.collabdesk.repository.DealRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Business rules for the deal lifecycle.
 * Every status change goes through {@link #transition} so that the state machine check
 * and the audit history can never be skipped.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class DealService {

    private static final int SCRIPT_LEAD_DAYS = 5;

    private final DealRepository deals;
    private final BrandProfileRepository brands;
    private final CreatorProfileRepository creators;
    private final Clock clock;

    // ---------- queries ----------

    @Transactional(readOnly = true)
    public List<Deal> listFor(User user, DealStatus status) {
        List<Deal> result = switch (user.getRole()) {
            case CREATOR -> deals.findByCreatorIdOrderByUpdatedAtDesc(creatorOf(user).getId());
            case BRAND -> deals.findByBrandIdOrderByUpdatedAtDesc(brandOf(user).getId());
            case ADMIN -> deals.findAllByOrderByUpdatedAtDesc();
        };
        return status == null ? result : result.stream().filter(d -> d.getStatus() == status).toList();
    }

    public Deal getFor(User user, Long dealId) {
        Deal deal = deals.findById(dealId).orElseThrow(() -> new NotFoundException("Deal " + dealId + " not found"));
        checkOwnership(user, deal);
        return deal;
    }

    // ---------- brand actions ----------

    public Deal propose(User brandUser, DealRequests.CreateDeal req) {
        BrandProfile brand = brandOf(brandUser);
        CreatorProfile creator = creators.findById(req.creatorId())
                .orElseThrow(() -> new NotFoundException("Creator " + req.creatorId() + " not found"));

        Deal deal = new Deal();
        deal.setBrand(brand);
        deal.setCreator(creator);
        deal.setTitle(req.title());
        deal.setBrief(req.brief());
        deal.setBudget(req.budget());
        deal.setDueDate(req.dueDate());
        if (req.maxRevisions() != null) deal.setMaxRevisions(req.maxRevisions());
        if (req.paymentTermDays() != null) deal.setPaymentTermDays(req.paymentTermDays());
        deal.addHistory(new DealStatusHistory(null, DealStatus.PROPOSED, brandUser, "Proposal sent"));
        return deals.save(deal);
    }

    public Deal cancel(User brandUser, Long dealId, String note) {
        Deal deal = getAs(brandUser, dealId, Role.BRAND);
        return transition(deal, DealStatus.CANCELLED, brandUser, note);
    }

    public Deal confirmPayment(User brandUser, Long dealId) {
        Deal deal = getAs(brandUser, dealId, Role.BRAND);
        deal.setPaymentOverdue(false);
        return transition(deal, DealStatus.PAID, brandUser, "Payment confirmed: " + deal.getAgreedAmount());
    }

    // ---------- creator actions ----------

    public Deal counter(User creatorUser, Long dealId, DealRequests.Counter req) {
        Deal deal = getAs(creatorUser, dealId, Role.CREATOR);
        if (req.amount().compareTo(deal.getBudget()) == 0) {
            throw new BusinessRuleException("Counter amount is the same as the budget, just accept the proposal");
        }
        deal.setCounterAmount(req.amount());
        String note = "Counter-offer " + req.amount() + (req.note() == null ? "" : " - " + req.note());
        return transition(deal, DealStatus.COUNTERED, creatorUser, note);
    }

    public Deal reject(User creatorUser, Long dealId, String note) {
        Deal deal = getAs(creatorUser, dealId, Role.CREATOR);
        return transition(deal, DealStatus.REJECTED, creatorUser, note);
    }

    public Deal publish(User creatorUser, Long dealId, String url) {
        Deal deal = getAs(creatorUser, dealId, Role.CREATOR);
        if (!deal.isScriptApproved()) {
            throw new BusinessRuleException("The script must be approved by the brand before publishing");
        }
        LocalDate today = LocalDate.now(clock);
        deal.setPublishedUrl(url);
        deal.setPublishedDate(today);
        deal.setPaymentDueDate(today.plusDays(deal.getPaymentTermDays()));
        return transition(deal, DealStatus.PUBLISHED, creatorUser, "Published: " + url);
    }

    // ---------- shared: accept ----------

    /**
     * Creator accepts a PROPOSED deal (at the budget), or brand accepts a COUNTERED deal (at the counter amount).
     */
    public Deal accept(User user, Long dealId) {
        Deal deal = getFor(user, dealId);
        if (user.getRole() == Role.CREATOR && deal.getStatus() == DealStatus.PROPOSED) {
            deal.setAgreedAmount(deal.getBudget());
        } else if (user.getRole() == Role.BRAND && deal.getStatus() == DealStatus.COUNTERED) {
            deal.setAgreedAmount(deal.getCounterAmount());
        } else {
            throw new BusinessRuleException(user.getRole() + " cannot accept a deal in status " + deal.getStatus());
        }
        deal.addDeliverable(new Deliverable(DeliverableType.SCRIPT, scriptDueDate(deal)));
        return transition(deal, DealStatus.ACCEPTED, user, "Agreed at " + deal.getAgreedAmount());
    }

    // ---------- internals ----------

    Deal transition(Deal deal, DealStatus target, User by, String note) {
        DealStatus from = deal.getStatus();
        if (!from.canTransitionTo(target)) {
            throw new BusinessRuleException("Cannot move deal from " + from + " to " + target);
        }
        deal.setStatus(target);
        deal.addHistory(new DealStatusHistory(from, target, by, note));
        return deal;
    }

    private LocalDate scriptDueDate(Deal deal) {
        LocalDate today = LocalDate.now(clock);
        LocalDate due = deal.getDueDate().minusDays(SCRIPT_LEAD_DAYS);
        return due.isAfter(today) ? due : deal.getDueDate();
    }

    private Deal getAs(User user, Long dealId, Role required) {
        if (user.getRole() != required) {
            throw new ForbiddenException("Only a " + required + " can do this");
        }
        return getFor(user, dealId);
    }

    private void checkOwnership(User user, Deal deal) {
        boolean owns = switch (user.getRole()) {
            case ADMIN -> true;
            case CREATOR -> Objects.equals(deal.getCreator().getUser().getId(), user.getId());
            case BRAND -> Objects.equals(deal.getBrand().getUser().getId(), user.getId());
        };
        if (!owns) {
            throw new ForbiddenException("You do not have access to deal " + deal.getId());
        }
    }

    private CreatorProfile creatorOf(User user) {
        return creators.findByUserId(user.getId())
                .orElseThrow(() -> new NotFoundException("Creator profile missing for " + user.getUsername()));
    }

    private BrandProfile brandOf(User user) {
        if (user.getRole() != Role.BRAND) {
            throw new ForbiddenException("Only brands can do this");
        }
        return brands.findByUserId(user.getId())
                .orElseThrow(() -> new NotFoundException("Brand profile missing for " + user.getUsername()));
    }
}

package com.in6206.collabdesk.service;

import com.in6206.collabdesk.entity.*;
import com.in6206.collabdesk.exception.BusinessRuleException;
import com.in6206.collabdesk.exception.ForbiddenException;
import com.in6206.collabdesk.exception.NotFoundException;
import com.in6206.collabdesk.repository.DeliverableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliverableService {

    private final DeliverableRepository deliverables;
    private final DealService dealService;
    private final Clock clock;

    /** How long a brand can take back an approval (added after usability testing: mis-clicks on Approve). */
    @Value("${collabdesk.approval.undo-minutes:5}")
    private int undoMinutes;

    public Deliverable submit(User creatorUser, Long id, String content) {
        Deliverable d = load(creatorUser, id, Role.CREATOR);
        if (!d.getStatus().canSubmit()) {
            throw new BusinessRuleException("Cannot submit a deliverable that is " + d.getStatus());
        }
        d.setContent(content);
        d.setStatus(DeliverableStatus.SUBMITTED);
        d.setSubmittedAt(LocalDateTime.now(clock));
        return d;
    }

    public Deliverable approve(User brandUser, Long id) {
        Deliverable d = load(brandUser, id, Role.BRAND);
        requireSubmitted(d);
        d.setStatus(DeliverableStatus.APPROVED);
        d.setApprovedAt(LocalDateTime.now(clock));
        return d;
    }

    /** Puts an approved deliverable back to SUBMITTED, only within the undo window and before publishing. */
    public Deliverable undoApproval(User brandUser, Long id) {
        Deliverable d = load(brandUser, id, Role.BRAND);   // also checks the deal is still ACCEPTED
        if (d.getStatus() != DeliverableStatus.APPROVED) {
            throw new BusinessRuleException("Only an approved deliverable can be undone");
        }
        if (!canUndoApproval(d)) {
            throw new BusinessRuleException("The " + undoMinutes + "-minute undo window has passed");
        }
        d.setStatus(DeliverableStatus.SUBMITTED);
        d.setApprovedAt(null);
        return d;
    }

    @Transactional(readOnly = true)
    public boolean canUndoApproval(Deliverable d) {
        return d.getStatus() == DeliverableStatus.APPROVED
                && d.getApprovedAt() != null
                && d.getDeal().getStatus() == DealStatus.ACCEPTED
                && LocalDateTime.now(clock).isBefore(undoDeadline(d));
    }

    public LocalDateTime undoDeadline(Deliverable d) {
        return d.getApprovedAt() == null ? null : d.getApprovedAt().plusMinutes(undoMinutes);
    }

    public int getUndoMinutes() {
        return undoMinutes;
    }

    public Deliverable requestRevision(User brandUser, Long id, String feedback) {
        Deliverable d = load(brandUser, id, Role.BRAND);
        requireSubmitted(d);
        int limit = d.getDeal().getMaxRevisions();
        if (d.getRevisionCount() >= limit) {
            throw new BusinessRuleException("Revision limit reached (" + limit + " rounds agreed in this deal)");
        }
        d.setRevisionCount(d.getRevisionCount() + 1);
        d.setFeedback(feedback);
        d.setStatus(DeliverableStatus.REVISION_REQUESTED);
        return d;
    }

    private Deliverable load(User user, Long id, Role required) {
        if (user.getRole() != required) {
            throw new ForbiddenException("Only a " + required + " can do this");
        }
        Deliverable d = deliverables.findById(id)
                .orElseThrow(() -> new NotFoundException("Deliverable " + id + " not found"));
        // reuse the deal ownership check
        Deal deal = dealService.getFor(user, d.getDeal().getId());
        if (deal.getStatus() != DealStatus.ACCEPTED) {
            throw new BusinessRuleException("Deliverables can only change while the deal is ACCEPTED");
        }
        return d;
    }

    private void requireSubmitted(Deliverable d) {
        if (d.getStatus() != DeliverableStatus.SUBMITTED) {
            throw new BusinessRuleException("Only submitted deliverables can be reviewed (current: " + d.getStatus() + ")");
        }
    }
}

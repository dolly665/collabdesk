package com.in6206.collabdesk.service;

import com.in6206.collabdesk.entity.Deal;
import com.in6206.collabdesk.entity.DeliverableStatus;
import com.in6206.collabdesk.entity.Role;
import org.springframework.stereotype.Component;

/**
 * Works out "what should this user do next on this deal?".
 * Used by the dashboards and lists (Thymeleaf calls it as @dealActions).
 */
@Component("dealActions")
public class DealActions {

    public String nextAction(Deal d, Role role) {
        return switch (d.getStatus()) {
            case PROPOSED -> role == Role.CREATOR ? "Respond to proposal" : null;
            case COUNTERED -> role == Role.BRAND ? "Review counter-offer" : null;
            case ACCEPTED -> acceptedAction(d, role);
            case PUBLISHED -> role == Role.BRAND ? (d.isPaymentOverdue() ? "Payment overdue!" : "Confirm payment") : null;
            default -> null;
        };
    }

    private String acceptedAction(Deal d, Role role) {
        if (role == Role.BRAND) {
            boolean toReview = d.getDeliverables().stream().anyMatch(x -> x.getStatus() == DeliverableStatus.SUBMITTED);
            return toReview ? "Review script" : null;
        }
        if (role == Role.CREATOR) {
            if (d.isScriptApproved()) return "Publish video";
            boolean toSubmit = d.getDeliverables().stream().anyMatch(x -> x.getStatus().canSubmit());
            return toSubmit ? "Submit script" : null;
        }
        return null;
    }
}

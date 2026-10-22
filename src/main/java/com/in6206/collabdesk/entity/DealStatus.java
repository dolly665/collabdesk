package com.in6206.collabdesk.entity;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Deal lifecycle. The allowed transitions live here so the rules are in one place
 * and the service layer only has to ask "is this move legal?".
 */
public enum DealStatus {
    PROPOSED, COUNTERED, ACCEPTED, PUBLISHED, PAID, REJECTED, CANCELLED;

    private static final Map<DealStatus, Set<DealStatus>> ALLOWED = Map.of(
            PROPOSED,  EnumSet.of(ACCEPTED, COUNTERED, REJECTED, CANCELLED),
            COUNTERED, EnumSet.of(ACCEPTED, CANCELLED),
            ACCEPTED,  EnumSet.of(PUBLISHED, CANCELLED),
            PUBLISHED, EnumSet.of(PAID),
            PAID,      EnumSet.noneOf(DealStatus.class),
            REJECTED,  EnumSet.noneOf(DealStatus.class),
            CANCELLED, EnumSet.noneOf(DealStatus.class)
    );

    public boolean canTransitionTo(DealStatus target) {
        return ALLOWED.get(this).contains(target);
    }

    public boolean isClosed() {
        return this == PAID || this == REJECTED || this == CANCELLED;
    }
}

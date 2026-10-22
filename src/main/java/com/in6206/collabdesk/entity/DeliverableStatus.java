package com.in6206.collabdesk.entity;

public enum DeliverableStatus {
    PENDING, SUBMITTED, APPROVED, REVISION_REQUESTED, OVERDUE;

    /** Creator can (re)submit from these states. */
    public boolean canSubmit() {
        return this == PENDING || this == REVISION_REQUESTED || this == OVERDUE;
    }
}

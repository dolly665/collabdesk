package com.in6206.collabdesk;

import com.in6206.collabdesk.entity.DealStatus;
import org.junit.jupiter.api.Test;

import static com.in6206.collabdesk.entity.DealStatus.*;
import static org.junit.jupiter.api.Assertions.*;

/** Pure unit test of the state machine rules — no Spring context needed. */
class DealStatusTest {

    @Test
    void happyPathIsAllowed() {
        assertTrue(PROPOSED.canTransitionTo(COUNTERED));
        assertTrue(COUNTERED.canTransitionTo(ACCEPTED));
        assertTrue(ACCEPTED.canTransitionTo(PUBLISHED));
        assertTrue(PUBLISHED.canTransitionTo(PAID));
    }

    @Test
    void skippingStepsIsRejected() {
        assertFalse(PROPOSED.canTransitionTo(PAID));
        assertFalse(PROPOSED.canTransitionTo(PUBLISHED));
        assertFalse(COUNTERED.canTransitionTo(COUNTERED));
    }

    @Test
    void publishedDealCannotBeCancelled() {
        assertFalse(PUBLISHED.canTransitionTo(CANCELLED));
    }

    @Test
    void closedStatesAreFinal() {
        for (DealStatus closed : new DealStatus[]{PAID, REJECTED, CANCELLED}) {
            assertTrue(closed.isClosed());
            for (DealStatus target : DealStatus.values()) {
                assertFalse(closed.canTransitionTo(target), closed + " -> " + target);
            }
        }
    }
}

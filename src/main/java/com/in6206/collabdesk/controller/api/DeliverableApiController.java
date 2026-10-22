package com.in6206.collabdesk.controller.api;

import com.in6206.collabdesk.dto.DealRequests;
import com.in6206.collabdesk.dto.DealResponse;
import com.in6206.collabdesk.entity.Deliverable;
import com.in6206.collabdesk.service.CurrentUserService;
import com.in6206.collabdesk.service.DeliverableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/** Returns the whole parent deal after each action so the client sees the new state in one call. */
@RestController
@RequestMapping("/api/deliverables")
@RequiredArgsConstructor
public class DeliverableApiController {

    private final DeliverableService deliverableService;
    private final CurrentUserService currentUser;

    @PostMapping("/{id}/submit")
    @PreAuthorize("hasRole('CREATOR')")
    public DealResponse submit(Authentication auth, @PathVariable Long id, @Valid @RequestBody DealRequests.Submit req) {
        return toDeal(deliverableService.submit(currentUser.from(auth), id, req.content()));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('BRAND')")
    public DealResponse approve(Authentication auth, @PathVariable Long id) {
        return toDeal(deliverableService.approve(currentUser.from(auth), id));
    }

    @PostMapping("/{id}/undo-approve")
    @PreAuthorize("hasRole('BRAND')")
    public DealResponse undoApprove(Authentication auth, @PathVariable Long id) {
        return toDeal(deliverableService.undoApproval(currentUser.from(auth), id));
    }

    @PostMapping("/{id}/revise")
    @PreAuthorize("hasRole('BRAND')")
    public DealResponse revise(Authentication auth, @PathVariable Long id, @Valid @RequestBody DealRequests.Revise req) {
        return toDeal(deliverableService.requestRevision(currentUser.from(auth), id, req.feedback()));
    }

    private DealResponse toDeal(Deliverable d) {
        return DealResponse.of(d.getDeal());
    }
}

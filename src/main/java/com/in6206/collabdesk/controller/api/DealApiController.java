package com.in6206.collabdesk.controller.api;

import com.in6206.collabdesk.dto.DealRequests;
import com.in6206.collabdesk.dto.DealResponse;
import com.in6206.collabdesk.entity.DealStatus;
import com.in6206.collabdesk.entity.User;
import com.in6206.collabdesk.service.CurrentUserService;
import com.in6206.collabdesk.service.DealService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deals")
@RequiredArgsConstructor
public class DealApiController {

    private final DealService dealService;
    private final CurrentUserService currentUser;

    @GetMapping
    public List<DealResponse> myDeals(Authentication auth, @RequestParam(required = false) DealStatus status) {
        return dealService.listFor(me(auth), status).stream().map(DealResponse::of).toList();
    }

    @GetMapping("/{id}")
    public DealResponse get(Authentication auth, @PathVariable Long id) {
        return DealResponse.of(dealService.getFor(me(auth), id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('BRAND')")
    public DealResponse propose(Authentication auth, @Valid @RequestBody DealRequests.CreateDeal req) {
        return DealResponse.of(dealService.propose(me(auth), req));
    }

    @PostMapping("/{id}/accept")
    public DealResponse accept(Authentication auth, @PathVariable Long id) {
        return DealResponse.of(dealService.accept(me(auth), id));
    }

    @PostMapping("/{id}/counter")
    @PreAuthorize("hasRole('CREATOR')")
    public DealResponse counter(Authentication auth, @PathVariable Long id, @Valid @RequestBody DealRequests.Counter req) {
        return DealResponse.of(dealService.counter(me(auth), id, req));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('CREATOR')")
    public DealResponse reject(Authentication auth, @PathVariable Long id, @Valid @RequestBody(required = false) DealRequests.Note req) {
        return DealResponse.of(dealService.reject(me(auth), id, req == null ? null : req.note()));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('BRAND')")
    public DealResponse cancel(Authentication auth, @PathVariable Long id, @Valid @RequestBody(required = false) DealRequests.Note req) {
        return DealResponse.of(dealService.cancel(me(auth), id, req == null ? null : req.note()));
    }

    @PostMapping("/{id}/publish")
    @PreAuthorize("hasRole('CREATOR')")
    public DealResponse publish(Authentication auth, @PathVariable Long id, @Valid @RequestBody DealRequests.Publish req) {
        return DealResponse.of(dealService.publish(me(auth), id, req.url()));
    }

    @PostMapping("/{id}/pay")
    @PreAuthorize("hasRole('BRAND')")
    public DealResponse pay(Authentication auth, @PathVariable Long id) {
        return DealResponse.of(dealService.confirmPayment(me(auth), id));
    }

    private User me(Authentication auth) {
        return currentUser.from(auth);
    }
}

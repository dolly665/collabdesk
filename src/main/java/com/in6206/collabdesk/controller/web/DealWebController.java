package com.in6206.collabdesk.controller.web;

import com.in6206.collabdesk.dto.DealRequests;
import com.in6206.collabdesk.entity.DealStatus;
import com.in6206.collabdesk.entity.User;
import com.in6206.collabdesk.exception.BusinessRuleException;
import com.in6206.collabdesk.exception.ForbiddenException;
import com.in6206.collabdesk.service.CurrentUserService;
import com.in6206.collabdesk.service.DealService;
import com.in6206.collabdesk.service.DeliverableService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

/**
 * Deal list, detail page and all deal / deliverable actions for the web UI.
 * Shared by creators and brands; DealService decides who may do what.
 */
@Controller
@RequiredArgsConstructor
public class DealWebController {

    private final DealService dealService;
    private final DeliverableService deliverableService;
    private final CurrentUserService currentUser;

    @GetMapping("/deals")
    public String list(Authentication auth, @RequestParam(required = false) DealStatus status, Model model) {
        model.addAttribute("deals", dealService.listFor(currentUser.from(auth), status));
        model.addAttribute("statuses", DealStatus.values());
        model.addAttribute("selected", status);
        return "deals/list";
    }

    @GetMapping("/deals/{id}")
    public String detail(Authentication auth, @PathVariable Long id, Model model) {
        model.addAttribute("deal", dealService.getFor(currentUser.from(auth), id));
        return "deals/detail";
    }

    // ---- deal actions ----

    @PostMapping("/deals/{id}/accept")
    public String accept(Authentication auth, @PathVariable Long id, RedirectAttributes ra) {
        return run(id, ra, "Deal accepted", () -> dealService.accept(me(auth), id));
    }

    @PostMapping("/deals/{id}/counter")
    public String counter(Authentication auth, @PathVariable Long id, @RequestParam BigDecimal amount,
                          @RequestParam(required = false) String note, RedirectAttributes ra) {
        if (amount == null || amount.signum() <= 0) {
            ra.addFlashAttribute("error", "Counter amount must be positive");
            return "redirect:/deals/" + id;
        }
        return run(id, ra, "Counter-offer sent", () -> dealService.counter(me(auth), id, new DealRequests.Counter(amount, note)));
    }

    @PostMapping("/deals/{id}/reject")
    public String reject(Authentication auth, @PathVariable Long id, @RequestParam(required = false) String note,
                         RedirectAttributes ra) {
        return run(id, ra, "Proposal rejected", () -> dealService.reject(me(auth), id, note));
    }

    @PostMapping("/deals/{id}/cancel")
    public String cancel(Authentication auth, @PathVariable Long id, @RequestParam(required = false) String note,
                         RedirectAttributes ra) {
        return run(id, ra, "Deal cancelled", () -> dealService.cancel(me(auth), id, note));
    }

    @PostMapping("/deals/{id}/publish")
    public String publish(Authentication auth, @PathVariable Long id, @RequestParam String url, RedirectAttributes ra) {
        if (url == null || !url.matches("^https?://.+")) {
            ra.addFlashAttribute("error", "Please enter a valid link starting with http:// or https://");
            return "redirect:/deals/" + id;
        }
        return run(id, ra, "Marked as published. Payment is now due.", () -> dealService.publish(me(auth), id, url));
    }

    @PostMapping("/deals/{id}/pay")
    public String pay(Authentication auth, @PathVariable Long id, RedirectAttributes ra) {
        return run(id, ra, "Payment confirmed. Deal closed.", () -> dealService.confirmPayment(me(auth), id));
    }

    // ---- deliverable actions (dealId is only used to redirect back) ----

    @PostMapping("/deals/{dealId}/deliverables/{id}/submit")
    public String submit(Authentication auth, @PathVariable Long dealId, @PathVariable Long id,
                         @RequestParam String content, RedirectAttributes ra) {
        if (content == null || content.isBlank()) {
            ra.addFlashAttribute("error", "Content cannot be empty");
            return "redirect:/deals/" + dealId;
        }
        return run(dealId, ra, "Submitted for review", () -> deliverableService.submit(me(auth), id, content));
    }

    @PostMapping("/deals/{dealId}/deliverables/{id}/approve")
    public String approve(Authentication auth, @PathVariable Long dealId, @PathVariable Long id, RedirectAttributes ra) {
        return run(dealId, ra, "Approved. Clicked by mistake? You can undo it for "
                + deliverableService.getUndoMinutes() + " minutes.", () -> deliverableService.approve(me(auth), id));
    }

    @PostMapping("/deals/{dealId}/deliverables/{id}/undo-approve")
    public String undoApprove(Authentication auth, @PathVariable Long dealId, @PathVariable Long id, RedirectAttributes ra) {
        return run(dealId, ra, "Approval undone. The deliverable is back in review.",
                () -> deliverableService.undoApproval(me(auth), id));
    }

    @PostMapping("/deals/{dealId}/deliverables/{id}/revise")
    public String revise(Authentication auth, @PathVariable Long dealId, @PathVariable Long id,
                         @RequestParam String feedback, RedirectAttributes ra) {
        if (feedback == null || feedback.isBlank()) {
            ra.addFlashAttribute("error", "Please write what needs to change");
            return "redirect:/deals/" + dealId;
        }
        return run(dealId, ra, "Revision requested", () -> deliverableService.requestRevision(me(auth), id, feedback));
    }

    /** Runs an action and turns business-rule errors into a red message instead of an error page. */
    private String run(Long dealId, RedirectAttributes ra, String successMsg, Runnable action) {
        try {
            action.run();
            ra.addFlashAttribute("success", successMsg);
        } catch (BusinessRuleException | ForbiddenException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/deals/" + dealId;
    }

    private User me(Authentication auth) {
        return currentUser.from(auth);
    }
}

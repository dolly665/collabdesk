package com.in6206.collabdesk.controller.web;

import com.in6206.collabdesk.exception.BusinessRuleException;
import com.in6206.collabdesk.scheduler.OverdueScheduler;
import com.in6206.collabdesk.service.AccountService;
import com.in6206.collabdesk.service.CurrentUserService;
import com.in6206.collabdesk.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminWebController {

    private final StatsService statsService;
    private final AccountService accountService;
    private final OverdueScheduler overdueScheduler;
    private final CurrentUserService currentUser;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("stats", statsService.platformStats());
        return "admin/dashboard";
    }

    @PostMapping("/run-overdue-check")
    public String runOverdue(RedirectAttributes ra) {
        int flagged = overdueScheduler.runChecks();
        ra.addFlashAttribute("success", "Overdue check finished: " + flagged + " item(s) newly flagged");
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", accountService.allUsers());
        return "admin/users";
    }

    @PostMapping("/users/{id}/toggle")
    public String toggle(Authentication auth, @PathVariable Long id, RedirectAttributes ra) {
        try {
            var u = accountService.toggleEnabled(currentUser.from(auth), id);
            ra.addFlashAttribute("success", u.getUsername() + (u.isEnabled() ? " enabled" : " disabled"));
        } catch (BusinessRuleException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }
}

package com.in6206.collabdesk.controller.web;

import com.in6206.collabdesk.dto.ProfileForm;
import com.in6206.collabdesk.entity.Platform;
import com.in6206.collabdesk.entity.User;
import com.in6206.collabdesk.service.AccountService;
import com.in6206.collabdesk.service.CurrentUserService;
import com.in6206.collabdesk.service.DashboardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/creator")
@RequiredArgsConstructor
public class CreatorWebController {

    private final DashboardService dashboardService;
    private final AccountService accountService;
    private final CurrentUserService currentUser;

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        User me = currentUser.from(auth);
        model.addAttribute("summary", dashboardService.summaryFor(me));
        model.addAttribute("profile", accountService.creatorProfile(me));
        return "creator/dashboard";
    }

    @GetMapping("/profile")
    public String profile(Authentication auth, Model model) {
        model.addAttribute("form", ProfileForm.from(accountService.creatorProfile(currentUser.from(auth))));
        model.addAttribute("platforms", Platform.values());
        return "creator/profile";
    }

    @PostMapping("/profile")
    public String saveProfile(Authentication auth, @Valid @ModelAttribute("form") ProfileForm form,
                              BindingResult result, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("platforms", Platform.values());
            return "creator/profile";
        }
        accountService.updateCreatorProfile(currentUser.from(auth), form);
        ra.addFlashAttribute("success", "Profile saved");
        return "redirect:/creator/profile";
    }
}

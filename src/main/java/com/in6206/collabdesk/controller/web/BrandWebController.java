package com.in6206.collabdesk.controller.web;

import com.in6206.collabdesk.dto.ProposalForm;
import com.in6206.collabdesk.entity.Deal;
import com.in6206.collabdesk.entity.Platform;
import com.in6206.collabdesk.exception.NotFoundException;
import com.in6206.collabdesk.repository.CreatorProfileRepository;
import com.in6206.collabdesk.service.CreatorService;
import com.in6206.collabdesk.service.CurrentUserService;
import com.in6206.collabdesk.service.DashboardService;
import com.in6206.collabdesk.service.DealService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/brand")
@RequiredArgsConstructor
public class BrandWebController {

    private final DashboardService dashboardService;
    private final CreatorService creatorService;
    private final CreatorProfileRepository creators;
    private final DealService dealService;
    private final CurrentUserService currentUser;

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        model.addAttribute("summary", dashboardService.summaryFor(currentUser.from(auth)));
        return "brand/dashboard";
    }

    @GetMapping("/creators")
    public String creators(@RequestParam(required = false) Platform platform,
                           @RequestParam(required = false) String niche,
                           @RequestParam(required = false) BigDecimal maxRate, Model model) {
        model.addAttribute("creators", creatorService.search(platform, niche, maxRate));
        model.addAttribute("platforms", Platform.values());
        model.addAttribute("platform", platform);
        model.addAttribute("niche", niche);
        model.addAttribute("maxRate", maxRate);
        return "brand/creators";
    }

    @GetMapping("/deals/new")
    public String newDeal(@RequestParam Long creatorId, Model model) {
        ProposalForm form = new ProposalForm();
        form.setCreatorId(creatorId);
        model.addAttribute("form", form);
        model.addAttribute("creator", creators.findById(creatorId)
                .orElseThrow(() -> new NotFoundException("Creator not found")));
        return "brand/new-deal";
    }

    @PostMapping("/deals")
    public String createDeal(Authentication auth, @Valid @ModelAttribute("form") ProposalForm form,
                             BindingResult result, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("creator", creators.findById(form.getCreatorId())
                    .orElseThrow(() -> new NotFoundException("Creator not found")));
            return "brand/new-deal";
        }
        Deal deal = dealService.propose(currentUser.from(auth), form.toRequest());
        ra.addFlashAttribute("success", "Proposal sent to " + deal.getCreator().getHandle());
        return "redirect:/deals/" + deal.getId();
    }
}

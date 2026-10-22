package com.in6206.collabdesk.controller.web;

import com.in6206.collabdesk.dto.RegisterForm;
import com.in6206.collabdesk.entity.Platform;
import com.in6206.collabdesk.exception.BusinessRuleException;
import com.in6206.collabdesk.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class RegisterController {

    private final AccountService accountService;

    @GetMapping("/register")
    public String form(Model model) {
        model.addAttribute("form", new RegisterForm());
        model.addAttribute("platforms", Platform.values());
        return "register";
    }

    @PostMapping("/register")
    public String submit(@Valid @ModelAttribute("form") RegisterForm form, BindingResult result,
                         Model model, RedirectAttributes ra) {
        if (!result.hasErrors()) {
            try {
                accountService.register(form);
                ra.addFlashAttribute("success", "Account created. Please sign in.");
                return "redirect:/login";
            } catch (BusinessRuleException e) {
                model.addAttribute("error", e.getMessage());
            }
        }
        model.addAttribute("platforms", Platform.values());
        return "register";
    }
}

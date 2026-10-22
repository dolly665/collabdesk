package com.in6206.collabdesk.controller.web;

import com.in6206.collabdesk.entity.Role;
import com.in6206.collabdesk.entity.User;
import com.in6206.collabdesk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/** Puts the logged-in user and role flags into every page's model (used by the nav bar). */
@ControllerAdvice(basePackages = "com.in6206.collabdesk.controller.web")
@RequiredArgsConstructor
public class GlobalModelAdvice {

    private final UserRepository users;

    @ModelAttribute
    public void addUser(Authentication auth, Model model) {
        if (auth == null || auth instanceof AnonymousAuthenticationToken) {
            return;
        }
        users.findByUsername(auth.getName()).ifPresent(u -> {
            model.addAttribute("me", u);
            model.addAttribute("isCreator", u.getRole() == Role.CREATOR);
            model.addAttribute("isBrand", u.getRole() == Role.BRAND);
            model.addAttribute("isAdmin", u.getRole() == Role.ADMIN);
        });
    }
}

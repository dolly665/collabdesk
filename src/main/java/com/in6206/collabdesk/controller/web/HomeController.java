package com.in6206.collabdesk.controller.web;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Sends each role to its own dashboard after login. */
@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Authentication auth) {
        String role = auth.getAuthorities().iterator().next().getAuthority();
        return switch (role) {
            case "ROLE_CREATOR" -> "redirect:/creator/dashboard";
            case "ROLE_BRAND" -> "redirect:/brand/dashboard";
            default -> "redirect:/admin/dashboard";
        };
    }
}

package com.in6206.collabdesk.controller.web;

import com.in6206.collabdesk.exception.ForbiddenException;
import com.in6206.collabdesk.exception.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Friendly error pages for the web UI (the REST API has its own JSON handler). */
@ControllerAdvice(basePackages = "com.in6206.collabdesk.controller.web")
public class WebExceptionHandler {

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String forbidden(ForbiddenException e, HttpServletRequest req, Model model) {
        model.addAttribute("path", req.getRequestURI());
        model.addAttribute("message", e.getMessage());
        return "error/403";
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String notFound(NotFoundException e, HttpServletRequest req, Model model) {
        model.addAttribute("path", req.getRequestURI());
        model.addAttribute("message", e.getMessage());
        return "error/404";
    }
}

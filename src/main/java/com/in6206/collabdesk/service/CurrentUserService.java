package com.in6206.collabdesk.service;

import com.in6206.collabdesk.entity.User;
import com.in6206.collabdesk.exception.NotFoundException;
import com.in6206.collabdesk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository users;

    public User from(Authentication auth) {
        return users.findByUsername(auth.getName())
                .orElseThrow(() -> new NotFoundException("User not found: " + auth.getName()));
    }
}

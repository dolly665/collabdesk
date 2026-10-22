package com.in6206.collabdesk.service;

import com.in6206.collabdesk.dto.ProfileForm;
import com.in6206.collabdesk.dto.RegisterForm;
import com.in6206.collabdesk.entity.*;
import com.in6206.collabdesk.exception.BusinessRuleException;
import com.in6206.collabdesk.exception.NotFoundException;
import com.in6206.collabdesk.repository.BrandProfileRepository;
import com.in6206.collabdesk.repository.CreatorProfileRepository;
import com.in6206.collabdesk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/** Registration, profile editing and admin user management. */
@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {

    private final UserRepository users;
    private final CreatorProfileRepository creators;
    private final BrandProfileRepository brands;
    private final PasswordEncoder encoder;

    public User register(RegisterForm f) {
        if (users.existsByUsername(f.getUsername())) {
            throw new BusinessRuleException("Username '" + f.getUsername() + "' is already taken");
        }
        if (users.existsByEmail(f.getEmail())) {
            throw new BusinessRuleException("Email is already registered");
        }
        if (f.getRole() == Role.ADMIN) {
            throw new BusinessRuleException("Admin accounts cannot be self-registered");
        }
        User u = users.save(new User(f.getUsername(), encoder.encode(f.getPassword()),
                f.getDisplayName(), f.getEmail(), f.getRole()));

        if (f.getRole() == Role.CREATOR) {
            if (f.getPlatform() == null || isBlank(f.getHandle())) {
                throw new BusinessRuleException("Creators need a platform and a handle");
            }
            CreatorProfile c = new CreatorProfile();
            c.setUser(u);
            c.setPlatform(f.getPlatform());
            c.setHandle(f.getHandle());
            c.setFollowerCount(f.getFollowerCount() == null ? 0 : f.getFollowerCount());
            c.setNiche(f.getNiche());
            c.setBaseRate(f.getBaseRate() == null ? BigDecimal.ZERO : f.getBaseRate());
            creators.save(c);
        } else {
            if (isBlank(f.getCompanyName())) {
                throw new BusinessRuleException("Brands need a company name");
            }
            BrandProfile b = new BrandProfile();
            b.setUser(u);
            b.setCompanyName(f.getCompanyName());
            b.setIndustry(f.getIndustry());
            brands.save(b);
        }
        return u;
    }

    @Transactional(readOnly = true)
    public CreatorProfile creatorProfile(User user) {
        return creators.findByUserId(user.getId())
                .orElseThrow(() -> new NotFoundException("Creator profile not found"));
    }

    public void updateCreatorProfile(User user, ProfileForm f) {
        CreatorProfile c = creatorProfile(user);
        c.getUser().setDisplayName(f.getDisplayName());
        c.setPlatform(f.getPlatform());
        c.setHandle(f.getHandle());
        c.setFollowerCount(f.getFollowerCount());
        c.setNiche(f.getNiche());
        c.setBaseRate(f.getBaseRate());
        c.setBio(f.getBio());
    }

    @Transactional(readOnly = true)
    public List<User> allUsers() {
        return users.findAll();
    }

    public User toggleEnabled(User admin, Long userId) {
        if (admin.getId().equals(userId)) {
            throw new BusinessRuleException("You cannot disable your own account");
        }
        User u = users.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        u.setEnabled(!u.isEnabled());
        return u;
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}

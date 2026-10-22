package com.in6206.collabdesk.dto;

import com.in6206.collabdesk.entity.CreatorProfile;
import com.in6206.collabdesk.entity.Platform;

import java.math.BigDecimal;

public record CreatorResponse(Long id, String displayName, Platform platform, String handle,
                              int followerCount, String niche, BigDecimal baseRate, String bio) {

    public static CreatorResponse of(CreatorProfile c) {
        return new CreatorResponse(c.getId(), c.getUser().getDisplayName(), c.getPlatform(), c.getHandle(),
                c.getFollowerCount(), c.getNiche(), c.getBaseRate(), c.getBio());
    }
}

package com.in6206.collabdesk.service;

import com.in6206.collabdesk.entity.CreatorProfile;
import com.in6206.collabdesk.entity.Platform;
import com.in6206.collabdesk.repository.CreatorProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CreatorService {

    private final CreatorProfileRepository creators;

    public List<CreatorProfile> search(Platform platform, String niche, BigDecimal maxRate) {
        String n = (niche == null || niche.isBlank()) ? null : niche.trim();
        return creators.search(platform, n, maxRate);
    }
}

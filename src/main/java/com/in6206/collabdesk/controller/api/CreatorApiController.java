package com.in6206.collabdesk.controller.api;

import com.in6206.collabdesk.dto.CreatorResponse;
import com.in6206.collabdesk.entity.Platform;
import com.in6206.collabdesk.service.CreatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/creators")
@RequiredArgsConstructor
public class CreatorApiController {

    private final CreatorService creatorService;

    @GetMapping
    public List<CreatorResponse> search(@RequestParam(required = false) Platform platform,
                                        @RequestParam(required = false) String niche,
                                        @RequestParam(required = false) BigDecimal maxRate) {
        return creatorService.search(platform, niche, maxRate).stream().map(CreatorResponse::of).toList();
    }
}

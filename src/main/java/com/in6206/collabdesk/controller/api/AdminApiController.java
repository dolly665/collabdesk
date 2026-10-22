package com.in6206.collabdesk.controller.api;

import com.in6206.collabdesk.dto.StatsResponse;
import com.in6206.collabdesk.scheduler.OverdueScheduler;
import com.in6206.collabdesk.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminApiController {

    private final StatsService statsService;
    private final OverdueScheduler overdueScheduler;

    @GetMapping("/stats")
    public StatsResponse stats() {
        return statsService.platformStats();
    }

    /** Runs the daily overdue job immediately (handy for the live demo). */
    @PostMapping("/run-overdue-check")
    public Map<String, Integer> runOverdueCheck() {
        return Map.of("flagged", overdueScheduler.runChecks());
    }
}

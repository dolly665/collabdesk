package com.in6206.collabdesk.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Request bodies for deal actions, grouped in one file to keep the package small. */
public final class DealRequests {

    private DealRequests() {
    }

    public record CreateDeal(
            @NotNull Long creatorId,
            @NotBlank @Size(max = 120) String title,
            @Size(max = 2000) String brief,
            @NotNull @Positive BigDecimal budget,
            @NotNull @Future LocalDate dueDate,
            @Min(0) @Max(5) Integer maxRevisions,
            @Min(0) @Max(120) Integer paymentTermDays) {
    }

    public record Counter(@NotNull @Positive BigDecimal amount, @Size(max = 500) String note) {
    }

    public record Note(@Size(max = 500) String note) {
    }

    public record Publish(@NotBlank @Pattern(regexp = "^https?://.+", message = "must be a valid http(s) URL") String url) {
    }

    public record Submit(@NotBlank @Size(max = 4000) String content) {
    }

    public record Revise(@NotBlank @Size(max = 1000) String feedback) {
    }
}

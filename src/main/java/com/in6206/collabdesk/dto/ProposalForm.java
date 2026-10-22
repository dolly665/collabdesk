package com.in6206.collabdesk.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ProposalForm {
    @NotNull
    private Long creatorId;
    @NotBlank @Size(max = 120)
    private String title;
    @Size(max = 2000)
    private String brief;
    @NotNull @Positive
    private BigDecimal budget;
    @NotNull @Future
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dueDate;
    @NotNull @Min(0) @Max(5)
    private Integer maxRevisions = 2;
    @NotNull @Min(0) @Max(120)
    private Integer paymentTermDays = 30;

    public DealRequests.CreateDeal toRequest() {
        return new DealRequests.CreateDeal(creatorId, title, brief, budget, dueDate, maxRevisions, paymentTermDays);
    }
}

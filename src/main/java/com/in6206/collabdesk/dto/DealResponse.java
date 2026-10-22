package com.in6206.collabdesk.dto;

import com.in6206.collabdesk.entity.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record DealResponse(
        Long id,
        String title,
        String brief,
        String brandName,
        String creatorHandle,
        Long creatorId,
        BigDecimal budget,
        BigDecimal counterAmount,
        BigDecimal agreedAmount,
        DealStatus status,
        LocalDate dueDate,
        int maxRevisions,
        int paymentTermDays,
        String publishedUrl,
        LocalDate paymentDueDate,
        boolean paymentOverdue,
        List<DeliverableResponse> deliverables,
        List<HistoryEntry> history) {

    public record DeliverableResponse(Long id, DeliverableType type, DeliverableStatus status, LocalDate dueDate,
                                      String content, String feedback, int revisionCount, LocalDateTime submittedAt) {
        static DeliverableResponse of(Deliverable d) {
            return new DeliverableResponse(d.getId(), d.getType(), d.getStatus(), d.getDueDate(),
                    d.getContent(), d.getFeedback(), d.getRevisionCount(), d.getSubmittedAt());
        }
    }

    public record HistoryEntry(DealStatus from, DealStatus to, String by, String note, LocalDateTime at) {
        static HistoryEntry of(DealStatusHistory h) {
            String by = h.getChangedBy() == null ? "system" : h.getChangedBy().getUsername();
            return new HistoryEntry(h.getFromStatus(), h.getToStatus(), by, h.getNote(), h.getChangedAt());
        }
    }

    public static DealResponse of(Deal d) {
        return new DealResponse(d.getId(), d.getTitle(), d.getBrief(),
                d.getBrand().getCompanyName(), d.getCreator().getHandle(), d.getCreator().getId(),
                d.getBudget(), d.getCounterAmount(), d.getAgreedAmount(), d.getStatus(), d.getDueDate(),
                d.getMaxRevisions(), d.getPaymentTermDays(), d.getPublishedUrl(), d.getPaymentDueDate(),
                d.isPaymentOverdue(),
                d.getDeliverables().stream().map(DeliverableResponse::of).toList(),
                d.getHistory().stream().map(HistoryEntry::of).toList());
    }
}

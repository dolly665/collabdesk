package com.in6206.collabdesk.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "deal_status_history")
@Getter
@Setter
@NoArgsConstructor
public class DealStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "deal_id", nullable = false)
    private Deal deal;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status", length = 20)
    private DealStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", nullable = false, length = 20)
    private DealStatus toStatus;

    /** Null when the change was made by the system (e.g. scheduled job). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by")
    private User changedBy;

    @Column(length = 500)
    private String note;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt = LocalDateTime.now();

    public DealStatusHistory(DealStatus from, DealStatus to, User changedBy, String note) {
        this.fromStatus = from;
        this.toStatus = to;
        this.changedBy = changedBy;
        this.note = note;
    }
}

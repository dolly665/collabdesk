package com.in6206.collabdesk.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "deliverables")
@Getter
@Setter
@NoArgsConstructor
public class Deliverable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "deal_id", nullable = false)
    private Deal deal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DeliverableType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DeliverableStatus status = DeliverableStatus.PENDING;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    /** Script text, or a link to the draft video. */
    @Column(length = 4000)
    private String content;

    @Column(length = 1000)
    private String feedback;

    @Column(name = "revision_count", nullable = false)
    private int revisionCount = 0;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    /** Set when the brand approves; used for the short "undo approval" window. */
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    public Deliverable(DeliverableType type, LocalDate dueDate) {
        this.type = type;
        this.dueDate = dueDate;
    }
}

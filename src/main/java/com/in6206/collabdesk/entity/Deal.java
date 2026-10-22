package com.in6206.collabdesk.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "deals")
@Getter
@Setter
@NoArgsConstructor
public class Deal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private BrandProfile brand;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private CreatorProfile creator;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(length = 2000)
    private String brief;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal budget;

    @Column(name = "counter_amount", precision = 10, scale = 2)
    private BigDecimal counterAmount;

    @Column(name = "agreed_amount", precision = 10, scale = 2)
    private BigDecimal agreedAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DealStatus status = DealStatus.PROPOSED;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "max_revisions", nullable = false)
    private int maxRevisions = 2;

    @Column(name = "payment_term_days", nullable = false)
    private int paymentTermDays = 30;

    @Column(name = "published_url", length = 300)
    private String publishedUrl;

    @Column(name = "published_date")
    private LocalDate publishedDate;

    @Column(name = "payment_due_date")
    private LocalDate paymentDueDate;

    @Column(name = "payment_overdue", nullable = false)
    private boolean paymentOverdue = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "deal", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<Deliverable> deliverables = new ArrayList<>();

    @OneToMany(mappedBy = "deal", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("changedAt ASC")
    private List<DealStatusHistory> history = new ArrayList<>();

    @PreUpdate
    void touch() {
        updatedAt = LocalDateTime.now();
    }

    public void addDeliverable(Deliverable d) {
        d.setDeal(this);
        deliverables.add(d);
    }

    public void addHistory(DealStatusHistory h) {
        h.setDeal(this);
        history.add(h);
    }

    /** Publishing is only allowed once the brand has approved the script. */
    public boolean isScriptApproved() {
        return deliverables.stream()
                .anyMatch(d -> d.getType() == DeliverableType.SCRIPT && d.getStatus() == DeliverableStatus.APPROVED);
    }
}

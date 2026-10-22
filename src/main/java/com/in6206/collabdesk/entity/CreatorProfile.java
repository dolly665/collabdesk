package com.in6206.collabdesk.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "creator_profiles")
@Getter
@Setter
@NoArgsConstructor
public class CreatorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Platform platform;

    @Column(nullable = false, length = 60)
    private String handle;

    @Column(name = "follower_count")
    private int followerCount;

    @Column(length = 60)
    private String niche;

    @Column(name = "base_rate", precision = 10, scale = 2)
    private BigDecimal baseRate;

    @Column(length = 500)
    private String bio;
}

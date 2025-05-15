package com.ozcanpolat.twitter_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "retweets", schema = "twitter")
@Data
@NoArgsConstructor
public class Retweet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_tweet_id", nullable = false)
    private Tweet originalTweet;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(length = 280)
    private String additionalComment;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
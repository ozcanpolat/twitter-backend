package com.ozcanpolat.twitter_backend.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RetweetDto {
    private Long id;
    private Long userId;
    private String username;
    private Long originalTweetId;
    private LocalDateTime createdAt;

    @Size(max = 280, message = "Additional comment cannot exceed 280 characters")
    private String additionalComment;

    private TweetDto originalTweet;
}
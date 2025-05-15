package com.ozcanpolat.twitter_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TweetDto {
    private Long id;

    @NotBlank(message = "Tweet content cannot be empty")
    @Size(max = 280, message = "Tweet cannot exceed 280 characters")
    private String content;

    private String mediaUrl;

    private LocalDateTime createdAt;
    private Long userId;
    private String username;
    private int likesCount;
    private int commentsCount;
    private int retweetsCount;
    private boolean isLiked;
    private boolean isRetweeted;
}

package com.ozcanpolat.twitter_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class CommentDto {
    private Long id;

    @NotBlank(message = "Comment content cannot be empty")
    @Size(max = 280, message = "Comment cannot exceed 280 characters")
    private String content;

    private LocalDateTime createdAt;
    private Long userId;
    private String username;
    private Long tweetId;
}

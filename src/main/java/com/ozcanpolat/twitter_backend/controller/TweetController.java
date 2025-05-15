package com.ozcanpolat.twitter_backend.controller;

import com.ozcanpolat.twitter_backend.dto.TweetDto;
import com.ozcanpolat.twitter_backend.entity.Tweet;
import com.ozcanpolat.twitter_backend.service.TweetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tweet")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Tweet Controller", description = "Endpoints for managing tweets")
public class TweetController {

    @Autowired
    private TweetService tweetService;

    @PostMapping
    @Operation(summary = "Create a new tweet")
    public ResponseEntity<TweetDto> createTweet(
            @Valid @RequestBody TweetDto tweetDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        Tweet tweet = tweetService.createTweet(tweetDto, userDetails.getUsername());
        return ResponseEntity.ok(tweetService.convertToDto(tweet, userDetails.getUsername()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing tweet")
    public ResponseEntity<TweetDto> updateTweet(
            @PathVariable Long id,
            @Valid @RequestBody TweetDto tweetDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        Tweet tweet = tweetService.updateTweet(id, tweetDto, userDetails.getUsername());
        return ResponseEntity.ok(tweetService.convertToDto(tweet, userDetails.getUsername()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a tweet")
    public ResponseEntity<Void> deleteTweet(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        tweetService.deleteTweet(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a tweet by ID")
    public ResponseEntity<TweetDto> getTweet(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Tweet tweet = tweetService.findById(id);
        return ResponseEntity.ok(tweetService.convertToDto(tweet, userDetails.getUsername()));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get tweets by user ID")
    public ResponseEntity<List<TweetDto>> getUserTweets(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        List<Tweet> tweets = tweetService.findByUserId(userId);
        return ResponseEntity.ok(tweets.stream()
                .map(tweet -> tweetService.convertToDto(tweet, userDetails.getUsername()))
                .toList());
    }

    @GetMapping("/timeline")
    @Operation(summary = "Get user's timeline")
    public ResponseEntity<Page<TweetDto>> getTimeline(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<Tweet> timeline = tweetService.getTimeline(userDetails.getUsername(), pageable);
        return ResponseEntity.ok(tweetService.convertToDto(timeline, userDetails.getUsername()));
    }

    @GetMapping("/search")
    @Operation(summary = "Search tweets")
    public ResponseEntity<Page<TweetDto>> searchTweets(
            @RequestParam String keyword,
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<Tweet> tweets = tweetService.searchTweets(keyword, pageable);
        return ResponseEntity.ok(tweetService.convertToDto(tweets, userDetails.getUsername()));
    }

    @GetMapping("/media")
    @Operation(summary = "Get tweets with media")
    public ResponseEntity<Page<TweetDto>> getTweetsWithMedia(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<Tweet> tweets = tweetService.getTweetsWithMedia(pageable);
        return ResponseEntity.ok(tweetService.convertToDto(tweets, userDetails.getUsername()));
    }
}
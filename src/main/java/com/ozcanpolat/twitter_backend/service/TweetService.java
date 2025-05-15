package com.ozcanpolat.twitter_backend.service;

import com.ozcanpolat.twitter_backend.dto.TweetDto;
import com.ozcanpolat.twitter_backend.entity.Tweet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TweetService {
    Tweet createTweet(TweetDto tweetDto, String username);
    Tweet updateTweet(Long id, TweetDto tweetDto, String username);
    void deleteTweet(Long id, String username);
    Tweet findById(Long id);
    List<Tweet> findByUserId(Long userId);
    TweetDto convertToDto(Tweet tweet, String currentUsername);

    // New methods
    Page<Tweet> getTimeline(String username, Pageable pageable);
    Page<Tweet> searchTweets(String keyword, Pageable pageable);
    Page<Tweet> getTweetsWithMedia(Pageable pageable);
    Page<TweetDto> convertToDto(Page<Tweet> tweets, String currentUsername);
}
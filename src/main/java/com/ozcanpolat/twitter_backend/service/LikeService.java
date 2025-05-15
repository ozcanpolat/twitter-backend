package com.ozcanpolat.twitter_backend.service;

public interface LikeService {
    void likeTweet(Long tweetId, String username);
    void unlikeTweet(Long tweetId, String username);
    boolean hasUserLikedTweet(Long userId, Long tweetId);
}
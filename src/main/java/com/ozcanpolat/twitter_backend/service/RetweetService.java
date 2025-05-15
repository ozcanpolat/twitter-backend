package com.ozcanpolat.twitter_backend.service;

import com.ozcanpolat.twitter_backend.dto.RetweetDto;
import com.ozcanpolat.twitter_backend.entity.Retweet;

public interface RetweetService {
    Retweet createRetweet(RetweetDto retweetDto, String username);
    void deleteRetweet(Long id, String username);
    Retweet findById(Long id);
    boolean hasUserRetweeted(Long userId, Long tweetId);
    RetweetDto convertToDto(Retweet retweet);
}
package com.ozcanpolat.twitter_backend.repository;

import com.ozcanpolat.twitter_backend.entity.Retweet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RetweetRepository extends JpaRepository<Retweet, Long> {
    List<Retweet> findByUserId(Long userId);
    List<Retweet> findByOriginalTweetId(Long tweetId);
    Optional<Retweet> findByUserIdAndOriginalTweetId(Long userId, Long tweetId);
    boolean existsByUserIdAndOriginalTweetId(Long userId, Long tweetId);
}
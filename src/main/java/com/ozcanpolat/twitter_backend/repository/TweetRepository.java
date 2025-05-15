package com.ozcanpolat.twitter_backend.repository;

import com.ozcanpolat.twitter_backend.entity.Tweet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TweetRepository extends JpaRepository<Tweet, Long> {
    List<Tweet> findByUserId(Long userId);
    List<Tweet> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT t FROM Tweet t WHERE t.user.id IN :followingIds ORDER BY t.createdAt DESC")
    Page<Tweet> findTimelineByFollowingIds(@Param("followingIds") List<Long> followingIds, Pageable pageable);

    @Query("SELECT t FROM Tweet t WHERE LOWER(t.content) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY t.createdAt DESC")
    Page<Tweet> searchTweets(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT t, COUNT(l) AS likeCount, COUNT(r) AS retweetCount FROM Tweet t LEFT JOIN t.likes l LEFT JOIN t.retweets r GROUP BY t ORDER BY (COUNT(l) + COUNT(r)) DESC")
    Page<Object[]> findTrendingTweets(Pageable pageable);

    @Query("SELECT t FROM Tweet t WHERE t.mediaUrl IS NOT NULL ORDER BY t.createdAt DESC")
    Page<Tweet> findTweetsWithMedia(Pageable pageable);

}
// com.ozcanpolat.twitter_backend.repository.LikeRepository.java
package com.ozcanpolat.twitter_backend.repository;

import com.ozcanpolat.twitter_backend.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserIdAndTweetId(Long userId, Long tweetId);
    Optional<Like> findByUserIdAndCommentId(Long userId, Long commentId);
    boolean existsByUserIdAndTweetId(Long userId, Long tweetId);
    boolean existsByUserIdAndCommentId(Long userId, Long commentId);
    void deleteByUserIdAndTweetId(Long userId, Long tweetId);
    void deleteByUserIdAndCommentId(Long userId, Long commentId);

    @Query("SELECT COUNT(l) FROM Like l WHERE l.tweet.id = :tweetId")
    Long countByTweetId(@Param("tweetId") Long tweetId);

    @Query("SELECT COUNT(l) FROM Like l WHERE l.comment.id = :commentId")
    Long countByCommentId(@Param("commentId") Long commentId);
}
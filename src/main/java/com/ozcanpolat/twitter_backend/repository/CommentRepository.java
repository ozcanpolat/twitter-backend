// com.ozcanpolat.twitter_backend.repository.CommentRepository.java
package com.ozcanpolat.twitter_backend.repository;

import com.ozcanpolat.twitter_backend.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTweetId(Long tweetId);
    List<Comment> findByUserId(Long userId);

    @Query("SELECT c, COUNT(l) AS likeCount FROM Comment c LEFT JOIN c.likes l WHERE c.tweet.id = :tweetId GROUP BY c ORDER BY likeCount DESC")
    Page<Object[]> findByTweetIdOrderByLikesCount(@Param("tweetId") Long tweetId, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.parentComment.id = :parentId ORDER BY c.createdAt DESC")
    List<Comment> findRepliesByParentId(@Param("parentId") Long parentId);

    @Query("SELECT c FROM Comment c WHERE c.tweet.id = :tweetId AND c.parentComment IS NULL ORDER BY c.createdAt DESC")
    Page<Comment> findRootCommentsByTweetId(@Param("tweetId") Long tweetId, Pageable pageable);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.tweet.id = :tweetId")
    Long countByTweetId(@Param("tweetId") Long tweetId);

    @Query("SELECT c.user, COUNT(c) as commentCount FROM Comment c WHERE c.tweet.id = :tweetId GROUP BY c.user ORDER BY commentCount DESC")
    Page<Object[]> findMostActiveCommenters(@Param("tweetId") Long tweetId, Pageable pageable);

}
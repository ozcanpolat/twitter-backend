// com.ozcanpolat.twitter_backend.repository.UserRepository.java (devamı)
package com.ozcanpolat.twitter_backend.repository;

import com.ozcanpolat.twitter_backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<User> searchUsers(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT u FROM User u WHERE :userId IN (SELECT f.id FROM u.following f)")
    Page<User> findFollowers(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.id IN (SELECT f.id FROM User user JOIN user.following f WHERE user.id = :userId)")
    Page<User> findFollowing(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.id NOT IN (SELECT f.id FROM User user JOIN user.following f WHERE user.id = :userId) AND u.id != :userId")
    Page<User> findSuggestedUsers(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT COUNT(u) > 0 FROM User u JOIN u.following f WHERE u.id = :followerId AND f.id = :followingId")
    boolean isFollowing(@Param("followerId") Long followerId, @Param("followingId") Long followingId);

}
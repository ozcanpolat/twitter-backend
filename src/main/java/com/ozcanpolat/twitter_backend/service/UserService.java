package com.ozcanpolat.twitter_backend.service;

import com.ozcanpolat.twitter_backend.dto.UserDto;
import com.ozcanpolat.twitter_backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    User registerUser(UserDto userDto);
    User findByUsername(String username);
    User findById(Long id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // New methods for following functionality
    void followUser(String followerUsername, Long followingId);
    void unfollowUser(String followerUsername, Long followingId);
    boolean isFollowing(String followerUsername, Long followingId);
    Page<User> getFollowers(Long userId, Pageable pageable);
    Page<User> getFollowing(Long userId, Pageable pageable);

    // New methods for user search and suggestions
    Page<User> searchUsers(String keyword, Pageable pageable);
    Page<User> getSuggestedUsers(String username, Pageable pageable);

    // Profile management
    User updateProfile(String username, UserDto userDto);
    UserDto convertToDto(User user);
    Page<UserDto> convertToDto(Page<User> users);
}
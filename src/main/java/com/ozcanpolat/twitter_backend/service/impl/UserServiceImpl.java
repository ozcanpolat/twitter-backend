package com.ozcanpolat.twitter_backend.service.impl;

import com.ozcanpolat.twitter_backend.dto.UserDto;
import com.ozcanpolat.twitter_backend.entity.User;
import com.ozcanpolat.twitter_backend.exception.ResourceNotFoundException;
import com.ozcanpolat.twitter_backend.repository.UserRepository;
import com.ozcanpolat.twitter_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(UserDto userDto) {
        if (existsByUsername(userDto.getUsername())) {
            throw new IllegalStateException("Username already exists");
        }
        if (existsByEmail(userDto.getEmail())) {
            throw new IllegalStateException("Email already exists");
        }

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setName(userDto.getName());
        user.setBio(userDto.getBio());
        user.setProfileImageUrl(userDto.getProfileImageUrl());

        return userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public void followUser(String followerUsername, Long followingId) {
        User follower = findByUsername(followerUsername);
        User following = findById(followingId);

        if (follower.getId().equals(followingId)) {
            throw new IllegalStateException("Users cannot follow themselves");
        }

        follower.getFollowing().add(following);
        userRepository.save(follower);
    }

    @Override
    public void unfollowUser(String followerUsername, Long followingId) {
        User follower = findByUsername(followerUsername);
        User following = findById(followingId);

        if (!follower.getFollowing().remove(following)) {
            throw new IllegalStateException("User was not following the specified user");
        }

        userRepository.save(follower);
    }

    @Override
    public boolean isFollowing(String followerUsername, Long followingId) {
        User follower = findByUsername(followerUsername);
        return userRepository.isFollowing(follower.getId(), followingId);
    }

    @Override
    public Page<User> getFollowers(Long userId, Pageable pageable) {
        return userRepository.findFollowers(userId, pageable);
    }

    @Override
    public Page<User> getFollowing(Long userId, Pageable pageable) {
        return userRepository.findFollowing(userId, pageable);
    }

    @Override
    public Page<User> searchUsers(String keyword, Pageable pageable) {
        return userRepository.searchUsers(keyword, pageable);
    }

    @Override
    public Page<User> getSuggestedUsers(String username, Pageable pageable) {
        User user = findByUsername(username);
        return userRepository.findSuggestedUsers(user.getId(), pageable);
    }

    @Override
    public User updateProfile(String username, UserDto userDto) {
        User user = findByUsername(username);

        if (userDto.getUsername() != null && !userDto.getUsername().equals(username)) {
            if (existsByUsername(userDto.getUsername())) {
                throw new IllegalStateException("Username already exists");
            }
            user.setUsername(userDto.getUsername());
        }

        if (userDto.getEmail() != null && !userDto.getEmail().equals(user.getEmail())) {
            if (existsByEmail(userDto.getEmail())) {
                throw new IllegalStateException("Email already exists");
            }
            user.setEmail(userDto.getEmail());
        }

        if (userDto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        if (userDto.getBio() != null) {
            user.setBio(userDto.getBio());
        }

        if (userDto.getProfileImageUrl() != null) {
            user.setProfileImageUrl(userDto.getProfileImageUrl());
        }

        return userRepository.save(user);
    }

    @Override
    public UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setBio(user.getBio());
        dto.setProfileImageUrl(user.getProfileImageUrl());
        dto.setFollowersCount(user.getFollowers().size());
        dto.setFollowingCount(user.getFollowing().size());
        dto.setTweetsCount(user.getTweets().size());
        return dto;
    }

    @Override
    public Page<UserDto> convertToDto(Page<User> users) {
        return users.map(this::convertToDto);
    }
}
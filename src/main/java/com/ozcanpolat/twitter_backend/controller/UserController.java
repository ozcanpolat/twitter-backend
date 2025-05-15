package com.ozcanpolat.twitter_backend.controller;

import com.ozcanpolat.twitter_backend.dto.UserDto;
import com.ozcanpolat.twitter_backend.entity.User;
import com.ozcanpolat.twitter_backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Controller", description = "Endpoints for managing users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<UserDto> registerUser(@Valid @RequestBody UserDto userDto) {
        User user = userService.registerUser(userDto);
        return ResponseEntity.ok(userService.convertToDto(user));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(userService.convertToDto(user));
    }

    @GetMapping("/profile")
    @Operation(summary = "Get current user's profile")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<UserDto> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        return ResponseEntity.ok(userService.convertToDto(user));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update current user's profile")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<UserDto> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UserDto userDto) {
        User user = userService.updateProfile(userDetails.getUsername(), userDto);
        return ResponseEntity.ok(userService.convertToDto(user));
    }

    @PostMapping("/follow/{userId}")
    @Operation(summary = "Follow a user")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Void> followUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        userService.followUser(userDetails.getUsername(), userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unfollow/{userId}")
    @Operation(summary = "Unfollow a user")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Void> unfollowUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        userService.unfollowUser(userDetails.getUsername(), userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}/followers")
    @Operation(summary = "Get user's followers")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Page<UserDto>> getFollowers(
            @PathVariable Long userId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<User> followers = userService.getFollowers(userId, pageable);
        return ResponseEntity.ok(userService.convertToDto(followers));
    }

    @GetMapping("/{userId}/following")
    @Operation(summary = "Get users that a user is following")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Page<UserDto>> getFollowing(
            @PathVariable Long userId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<User> following = userService.getFollowing(userId, pageable);
        return ResponseEntity.ok(userService.convertToDto(following));
    }

    @GetMapping("/search")
    @Operation(summary = "Search users")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Page<UserDto>> searchUsers(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<User> users = userService.searchUsers(keyword, pageable);
        return ResponseEntity.ok(userService.convertToDto(users));
    }

    @GetMapping("/suggested")
    @Operation(summary = "Get suggested users to follow")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Page<UserDto>> getSuggestedUsers(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<User> users = userService.getSuggestedUsers(userDetails.getUsername(), pageable);
        return ResponseEntity.ok(userService.convertToDto(users));
    }

    @GetMapping("/check-following/{userId}")
    @Operation(summary = "Check if current user follows another user")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Boolean> isFollowing(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        boolean following = userService.isFollowing(userDetails.getUsername(), userId);
        return ResponseEntity.ok(following);
    }
}
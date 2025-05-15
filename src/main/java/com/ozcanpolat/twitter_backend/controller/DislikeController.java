package com.ozcanpolat.twitter_backend.controller;

import com.ozcanpolat.twitter_backend.entity.User;
import com.ozcanpolat.twitter_backend.exception.UnauthorizedException;
import com.ozcanpolat.twitter_backend.repository.LikeRepository;
import com.ozcanpolat.twitter_backend.repository.UserRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional; // Bu importu ekleyin
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dislike")
@SecurityRequirement(name = "Bearer Authentication")
public class DislikeController {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    @PostMapping("/")
    public ResponseEntity<?> dislikeTweet(@RequestParam Long tweetId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        if (!likeRepository.existsByUserIdAndTweetId(user.getId(), tweetId)) {
            return ResponseEntity.badRequest().body("Tweet not liked");
        }

        likeRepository.deleteByUserIdAndTweetId(user.getId(), tweetId);
        return ResponseEntity.ok().build();
    }
}
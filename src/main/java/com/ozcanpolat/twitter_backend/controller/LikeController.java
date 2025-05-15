package com.ozcanpolat.twitter_backend.controller;

import com.ozcanpolat.twitter_backend.entity.Like;
import com.ozcanpolat.twitter_backend.entity.Tweet;
import com.ozcanpolat.twitter_backend.entity.User;
import com.ozcanpolat.twitter_backend.exception.ResourceNotFoundException;
import com.ozcanpolat.twitter_backend.exception.UnauthorizedException;
import com.ozcanpolat.twitter_backend.repository.LikeRepository;
import com.ozcanpolat.twitter_backend.repository.TweetRepository;
import com.ozcanpolat.twitter_backend.repository.UserRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/like")
@SecurityRequirement(name = "Bearer Authentication")
public class LikeController {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/")
    public ResponseEntity<?> likeTweet(@RequestParam Long tweetId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new ResourceNotFoundException("Tweet not found"));

        if (likeRepository.existsByUserIdAndTweetId(user.getId(), tweetId)) {
            return ResponseEntity.badRequest().body("Tweet already liked");
        }

        Like like = new Like();
        like.setUser(user);
        like.setTweet(tweet);

        likeRepository.save(like);
        return ResponseEntity.ok().build();
    }
}
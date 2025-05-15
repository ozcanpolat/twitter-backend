package com.ozcanpolat.twitter_backend.controller;

import com.ozcanpolat.twitter_backend.dto.RetweetDto;
import com.ozcanpolat.twitter_backend.entity.Retweet;
import com.ozcanpolat.twitter_backend.entity.Tweet;
import com.ozcanpolat.twitter_backend.entity.User;
import com.ozcanpolat.twitter_backend.exception.ResourceNotFoundException;
import com.ozcanpolat.twitter_backend.exception.UnauthorizedException;
import com.ozcanpolat.twitter_backend.repository.RetweetRepository;
import com.ozcanpolat.twitter_backend.repository.TweetRepository;
import com.ozcanpolat.twitter_backend.repository.UserRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/retweet")
@SecurityRequirement(name = "Bearer Authentication")

public class RetweetController {

    @Autowired
    private RetweetRepository retweetRepository;

    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("")
    public ResponseEntity<RetweetDto> createRetweet(@Valid @RequestBody RetweetDto retweetDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        Tweet originalTweet = tweetRepository.findById(retweetDto.getOriginalTweetId())
                .orElseThrow(() -> new ResourceNotFoundException("Original tweet not found"));

        if (retweetRepository.existsByUserIdAndOriginalTweetId(user.getId(), originalTweet.getId())) {
            throw new RuntimeException("You have already retweeted this tweet");
        }

        Retweet retweet = new Retweet();
        retweet.setUser(user);
        retweet.setOriginalTweet(originalTweet);
        retweet.setAdditionalComment(retweetDto.getAdditionalComment());

        Retweet savedRetweet = retweetRepository.save(retweet);
        return ResponseEntity.ok(convertToDto(savedRetweet));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRetweet(@PathVariable Long id) {
        Retweet retweet = retweetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Retweet not found"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!retweet.getUser().getUsername().equals(auth.getName())) {
            throw new UnauthorizedException("You can only delete your own retweets");
        }

        retweetRepository.delete(retweet);
        return ResponseEntity.ok().build();
    }

    private RetweetDto convertToDto(Retweet retweet) {
        RetweetDto dto = new RetweetDto();
        dto.setId(retweet.getId());
        dto.setUserId(retweet.getUser().getId());
        dto.setUsername(retweet.getUser().getUsername());
        dto.setOriginalTweetId(retweet.getOriginalTweet().getId());
        dto.setCreatedAt(retweet.getCreatedAt());
        dto.setAdditionalComment(retweet.getAdditionalComment());
        return dto;
    }
}

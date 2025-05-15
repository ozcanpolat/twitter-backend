package com.ozcanpolat.twitter_backend.service.impl;

import com.ozcanpolat.twitter_backend.dto.RetweetDto;
import com.ozcanpolat.twitter_backend.entity.Retweet;
import com.ozcanpolat.twitter_backend.entity.Tweet;
import com.ozcanpolat.twitter_backend.entity.User;
import com.ozcanpolat.twitter_backend.exception.ResourceNotFoundException;
import com.ozcanpolat.twitter_backend.exception.UnauthorizedException;
import com.ozcanpolat.twitter_backend.repository.RetweetRepository;
import com.ozcanpolat.twitter_backend.service.RetweetService;
import com.ozcanpolat.twitter_backend.service.TweetService;
import com.ozcanpolat.twitter_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RetweetServiceImpl implements RetweetService {

    @Autowired
    private RetweetRepository retweetRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TweetService tweetService;

    @Override
    public Retweet createRetweet(RetweetDto retweetDto, String username) {
        User user = userService.findByUsername(username);
        Tweet originalTweet = tweetService.findById(retweetDto.getOriginalTweetId());

        if (retweetRepository.existsByUserIdAndOriginalTweetId(user.getId(), originalTweet.getId())) {
            throw new IllegalStateException("You have already retweeted this tweet");
        }

        Retweet retweet = new Retweet();
        retweet.setUser(user);
        retweet.setOriginalTweet(originalTweet);
        retweet.setAdditionalComment(retweetDto.getAdditionalComment());

        return retweetRepository.save(retweet);
    }

    @Override
    public void deleteRetweet(Long id, String username) {
        Retweet retweet = findById(id);

        if (!retweet.getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("You can only delete your own retweets");
        }

        retweetRepository.delete(retweet);
    }

    @Override
    public Retweet findById(Long id) {
        return retweetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Retweet not found with id: " + id));
    }

    @Override
    public boolean hasUserRetweeted(Long userId, Long tweetId) {
        return retweetRepository.existsByUserIdAndOriginalTweetId(userId, tweetId);
    }

    @Override
    public RetweetDto convertToDto(Retweet retweet) {
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
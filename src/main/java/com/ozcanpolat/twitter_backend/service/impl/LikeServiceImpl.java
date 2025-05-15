package com.ozcanpolat.twitter_backend.service.impl;

import com.ozcanpolat.twitter_backend.entity.Like;
import com.ozcanpolat.twitter_backend.entity.Tweet;
import com.ozcanpolat.twitter_backend.entity.User;
import com.ozcanpolat.twitter_backend.exception.ResourceNotFoundException;
import com.ozcanpolat.twitter_backend.repository.LikeRepository;
import com.ozcanpolat.twitter_backend.service.LikeService;
import com.ozcanpolat.twitter_backend.service.TweetService;
import com.ozcanpolat.twitter_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LikeServiceImpl implements LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TweetService tweetService;

    @Override
    public void likeTweet(Long tweetId, String username) {
        User user = userService.findByUsername(username);
        Tweet tweet = tweetService.findById(tweetId);

        if (likeRepository.existsByUserIdAndTweetId(user.getId(), tweetId)) {
            throw new IllegalStateException("Tweet already liked");
        }

        Like like = new Like();
        like.setUser(user);
        like.setTweet(tweet);

        likeRepository.save(like);
    }

    @Override
    public void unlikeTweet(Long tweetId, String username) {
        User user = userService.findByUsername(username);

        if (!likeRepository.existsByUserIdAndTweetId(user.getId(), tweetId)) {
            throw new ResourceNotFoundException("Like not found");
        }

        likeRepository.deleteByUserIdAndTweetId(user.getId(), tweetId);
    }

    @Override
    public boolean hasUserLikedTweet(Long userId, Long tweetId) {
        return likeRepository.existsByUserIdAndTweetId(userId, tweetId);
    }
}
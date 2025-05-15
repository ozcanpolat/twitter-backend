package com.ozcanpolat.twitter_backend.service.impl;

import com.ozcanpolat.twitter_backend.dto.TweetDto;
import com.ozcanpolat.twitter_backend.entity.Tweet;
import com.ozcanpolat.twitter_backend.entity.User;
import com.ozcanpolat.twitter_backend.exception.ResourceNotFoundException;
import com.ozcanpolat.twitter_backend.exception.UnauthorizedException;
import com.ozcanpolat.twitter_backend.repository.TweetRepository;
import com.ozcanpolat.twitter_backend.service.TweetService;
import com.ozcanpolat.twitter_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TweetServiceImpl implements TweetService {

    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private UserService userService;

    @Override
    public Tweet createTweet(TweetDto tweetDto, String username) {
        User user = userService.findByUsername(username);

        Tweet tweet = new Tweet();
        tweet.setContent(tweetDto.getContent());
        tweet.setUser(user);

        return tweetRepository.save(tweet);
    }

    @Override
    public Tweet updateTweet(Long id, TweetDto tweetDto, String username) {
        Tweet tweet = findById(id);

        if (!tweet.getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("You can only update your own tweets");
        }

        tweet.setContent(tweetDto.getContent());
        return tweetRepository.save(tweet);
    }

    @Override
    public void deleteTweet(Long id, String username) {
        Tweet tweet = findById(id);

        if (!tweet.getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("You can only delete your own tweets");
        }

        tweetRepository.delete(tweet);
    }

    @Override
    public Tweet findById(Long id) {
        return tweetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tweet not found with id: " + id));
    }

    @Override
    public List<Tweet> findByUserId(Long userId) {
        return tweetRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public Page<Tweet> getTimeline(String username, Pageable pageable) {
        User user = userService.findByUsername(username);
        List<Long> followingIds = user.getFollowing().stream()
                .map(User::getId)
                .collect(Collectors.toList());
        followingIds.add(user.getId()); // Include user's own tweets
        return tweetRepository.findTimelineByFollowingIds(followingIds, pageable);
    }

    @Override
    public Page<Tweet> searchTweets(String keyword, Pageable pageable) {
        return tweetRepository.searchTweets(keyword, pageable);
    }

    @Override
    public Page<Tweet> getTweetsWithMedia(Pageable pageable) {
        return tweetRepository.findTweetsWithMedia(pageable);
    }

    @Override
    public TweetDto convertToDto(Tweet tweet, String currentUsername) {
        TweetDto dto = new TweetDto();
        dto.setId(tweet.getId());
        dto.setContent(tweet.getContent());
        dto.setCreatedAt(tweet.getCreatedAt());
        dto.setUserId(tweet.getUser().getId());
        dto.setUsername(tweet.getUser().getUsername());
        dto.setLikesCount(tweet.getLikes().size());
        dto.setCommentsCount(tweet.getComments().size());
        dto.setRetweetsCount(tweet.getRetweets().size());

        if (currentUsername != null) {
            dto.setLiked(tweet.getLikes().stream()
                    .anyMatch(like -> like.getUser().getUsername().equals(currentUsername)));
            dto.setRetweeted(tweet.getRetweets().stream()
                    .anyMatch(retweet -> retweet.getUser().getUsername().equals(currentUsername)));
        }

        return dto;
    }

    @Override
    public Page<TweetDto> convertToDto(Page<Tweet> tweets, String currentUsername) {
        return tweets.map(tweet -> convertToDto(tweet, currentUsername));
    }
}
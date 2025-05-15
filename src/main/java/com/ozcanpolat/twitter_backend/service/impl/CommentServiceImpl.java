package com.ozcanpolat.twitter_backend.service.impl;

import com.ozcanpolat.twitter_backend.dto.CommentDto;
import com.ozcanpolat.twitter_backend.entity.Comment;
import com.ozcanpolat.twitter_backend.entity.Tweet;
import com.ozcanpolat.twitter_backend.entity.User;
import com.ozcanpolat.twitter_backend.exception.ResourceNotFoundException;
import com.ozcanpolat.twitter_backend.exception.UnauthorizedException;
import com.ozcanpolat.twitter_backend.repository.CommentRepository;
import com.ozcanpolat.twitter_backend.service.CommentService;
import com.ozcanpolat.twitter_backend.service.TweetService;
import com.ozcanpolat.twitter_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TweetService tweetService;

    @Override
    public Comment createComment(CommentDto commentDto, String username) {
        User user = userService.findByUsername(username);
        Tweet tweet = tweetService.findById(commentDto.getTweetId());

        Comment comment = new Comment();
        comment.setContent(commentDto.getContent());
        comment.setUser(user);
        comment.setTweet(tweet);

        return commentRepository.save(comment);
    }

    @Override
    public Comment updateComment(Long id, CommentDto commentDto, String username) {
        Comment comment = findById(id);

        if (!comment.getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("You can only update your own comments");
        }

        comment.setContent(commentDto.getContent());
        return commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Long id, String username) {
        Comment comment = findById(id);
        String tweetOwnerUsername = comment.getTweet().getUser().getUsername();
        String commentOwnerUsername = comment.getUser().getUsername();

        if (!commentOwnerUsername.equals(username) && !tweetOwnerUsername.equals(username)) {
            throw new UnauthorizedException("You can only delete your own comments or comments on your tweets");
        }

        commentRepository.delete(comment);
    }

    @Override
    public Comment findById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));
    }

    @Override
    public List<Comment> findByTweetId(Long tweetId) {
        return commentRepository.findByTweetId(tweetId);
    }

    @Override
    public CommentDto convertToDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUserId(comment.getUser().getId());
        dto.setUsername(comment.getUser().getUsername());
        dto.setTweetId(comment.getTweet().getId());
        return dto;
    }
}


package com.ozcanpolat.twitter_backend.service;

import com.ozcanpolat.twitter_backend.dto.CommentDto;
import com.ozcanpolat.twitter_backend.entity.Comment;

import java.util.List;

public interface CommentService {
    Comment createComment(CommentDto commentDto, String username);
    Comment updateComment(Long id, CommentDto commentDto, String username);
    void deleteComment(Long id, String username);
    Comment findById(Long id);
    List<Comment> findByTweetId(Long tweetId);
    CommentDto convertToDto(Comment comment);
}
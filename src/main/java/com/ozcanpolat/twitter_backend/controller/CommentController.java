package com.ozcanpolat.twitter_backend.controller;

import com.ozcanpolat.twitter_backend.dto.CommentDto;
import com.ozcanpolat.twitter_backend.entity.Comment;
import com.ozcanpolat.twitter_backend.entity.Tweet;
import com.ozcanpolat.twitter_backend.entity.User;
import com.ozcanpolat.twitter_backend.exception.ResourceNotFoundException;
import com.ozcanpolat.twitter_backend.exception.UnauthorizedException;
import com.ozcanpolat.twitter_backend.repository.CommentRepository;
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
@SecurityRequirement(name = "Bearer Authentication")
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/comment")
    public ResponseEntity<CommentDto> createComment(@Valid @RequestBody CommentDto commentDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        Tweet tweet = tweetRepository.findById(commentDto.getTweetId())
                .orElseThrow(() -> new ResourceNotFoundException("Tweet not found"));

        Comment comment = new Comment();
        comment.setContent(commentDto.getContent());
        comment.setUser(user);
        comment.setTweet(tweet);

        Comment savedComment = commentRepository.save(comment);
        return ResponseEntity.ok(convertToDto(savedComment));
    }

    @PutMapping("/comment/{id}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable Long id, @Valid @RequestBody CommentDto commentDto) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!comment.getUser().getUsername().equals(auth.getName())) {
            throw new UnauthorizedException("You can only update your own comments");
        }

        comment.setContent(commentDto.getContent());
        Comment updatedComment = commentRepository.save(comment);
        return ResponseEntity.ok(convertToDto(updatedComment));
    }

    @DeleteMapping("/comment/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        // Allow both comment owner and tweet owner to delete the comment
        if (!comment.getUser().getUsername().equals(username) &&
                !comment.getTweet().getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("You can only delete your own comments or comments on your tweets");
        }

        commentRepository.delete(comment);
        return ResponseEntity.ok().build();
    }

    private CommentDto convertToDto(Comment comment) {
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
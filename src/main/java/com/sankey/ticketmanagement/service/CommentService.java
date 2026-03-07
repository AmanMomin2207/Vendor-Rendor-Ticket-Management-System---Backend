package com.sankey.ticketmanagement.service;

import com.sankey.ticketmanagement.dto.AddCommentRequest;
import com.sankey.ticketmanagement.exception.ResourceNotFoundException;
import com.sankey.ticketmanagement.model.Comment;
import com.sankey.ticketmanagement.model.User;
import com.sankey.ticketmanagement.repository.CommentRepository;
import com.sankey.ticketmanagement.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository,
                          UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    public Comment addComment(String ticketId, String email, AddCommentRequest request) {

        if (request.getMessage() == null || request.getMessage().isBlank()) {
            throw new RuntimeException("Comment cannot be empty");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Comment comment = Comment.builder()
                .ticketId(ticketId)
                .authorId(user.getId())
                .authorName(user.getName())
                .authorRole(user.getRole())
                .message(request.getMessage())
                .createdAt(LocalDateTime.now())
                .build();

        return commentRepository.save(comment);
    }

    public List<Comment> getComments(String ticketId) {
        return commentRepository.findByTicketIdOrderByCreatedAtAsc(ticketId);
    }

    public void deleteComment(String commentId, String email) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // only author or admin can delete
        if (!comment.getAuthorId().equals(user.getId()) &&
                !user.getRole().name().equals("ADMIN")) {
            throw new RuntimeException("Not authorized to delete this comment");
        }

        commentRepository.delete(comment);
    }
}
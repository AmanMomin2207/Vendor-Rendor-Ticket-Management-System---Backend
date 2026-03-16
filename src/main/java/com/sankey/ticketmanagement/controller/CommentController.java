package com.sankey.ticketmanagement.controller;

import com.sankey.ticketmanagement.dto.AddCommentRequest;
import com.sankey.ticketmanagement.model.Comment;
import com.sankey.ticketmanagement.payload.ApiResponse;
import com.sankey.ticketmanagement.service.CommentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','BUYER','VENDOR')")
    @GetMapping("/{ticketId}/comments")
    public ApiResponse<List<Comment>> getComments(@PathVariable String ticketId) {
        return new ApiResponse<>(true, "Comments fetched",
                commentService.getComments(ticketId));
    }

    @PreAuthorize("hasAnyRole('ADMIN','BUYER','VENDOR')")
    @PostMapping("/{ticketId}/comments")
    public ApiResponse<Comment> addComment(@PathVariable String ticketId,
                                            Authentication authentication,
                                            @RequestBody AddCommentRequest request) {
        String email = authentication.getName();
        return new ApiResponse<>(true, "Comment added",
                commentService.addComment(ticketId, email, request));
    }

    @PreAuthorize("hasAnyRole('ADMIN','BUYER','VENDOR')")
    @DeleteMapping("/comments/{commentId}")
    public ApiResponse<Void> deleteComment(@PathVariable String commentId,
                                            Authentication authentication) {
        String email = authentication.getName();
        commentService.deleteComment(commentId, email);
        return new ApiResponse<>(true, "Comment deleted", null);
    }
}
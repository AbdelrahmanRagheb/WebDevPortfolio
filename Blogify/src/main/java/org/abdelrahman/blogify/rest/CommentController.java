package org.abdelrahman.blogify.rest;


import jakarta.validation.Valid;
import org.abdelrahman.blogify.entity.Comment;
import org.abdelrahman.blogify.models.CommentDTO;
import org.abdelrahman.blogify.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<String> addComment(@PathVariable Long postId, @RequestParam Long userId, @Valid @RequestBody Comment comment) {
        commentService.addComment(postId, userId, comment);
        return ResponseEntity.status(HttpStatus.CREATED).body("comment adedd successfully");
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentDTO>> getCommentsByPostId(@PathVariable Long postId) {
        List<CommentDTO> commentsByPostIds = commentService.getCommentsByPostIds(postId);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentsByPostIds);
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable Long commentId, @Valid @RequestBody Comment updatedComment) {
        CommentDTO savedComment = commentService.updateComment(commentId, updatedComment);
        return ResponseEntity.ok(savedComment);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok("comment with ID "+commentId+" deleted successfully");
    }


}


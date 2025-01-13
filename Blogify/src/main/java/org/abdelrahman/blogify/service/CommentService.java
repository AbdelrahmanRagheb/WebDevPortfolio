package org.abdelrahman.blogify.service;

import org.abdelrahman.blogify.entity.Comment;
import org.abdelrahman.blogify.entity.Post;
import org.abdelrahman.blogify.entity.User;
import org.abdelrahman.blogify.exception.UserNotFoundException;
import org.abdelrahman.blogify.models.CommentDTO;
import org.abdelrahman.blogify.models.PostSummaryDTO;
import org.abdelrahman.blogify.models.UserIdPostId;
import org.abdelrahman.blogify.repository.CommentRepository;
import org.abdelrahman.blogify.repository.PostRepository;
import org.abdelrahman.blogify.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }


    public void addComment(Long postId, Long userId, Comment comment) {
        Post post = postRepository.findAllPostInfoById(postId);
        User user = userRepository.findAllUserInfoById(userId);
        comment.setPost(post);
        comment.setUser(user);
        commentRepository.save(comment);
    }

    public Comment getCommentByPostId(Long postId) {
        return commentRepository.findById(postId).orElseThrow(() -> new UserNotFoundException("Comment Not Found"));
    }

    public List<CommentDTO> getCommentsByPostIds(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new UserNotFoundException("Post with ID " + postId + " does not exist");
        }
        return commentRepository.findAllCommentsByPostId(postId);
    }

    public CommentDTO updateComment(Long commentId, Comment comment) {
        Comment existingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new UserNotFoundException("Comment with ID " + commentId + " does not exist"));
        comment.setPost(existingComment.getPost());
        comment.setUser(existingComment.getUser());
        commentRepository.save(comment);
        return CommentDTO.convertToDTO(comment);
    }
    public void deleteComment(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new UserNotFoundException("Comment with ID " + commentId + " does not exist");
        }
        commentRepository.deleteById(commentId);

    }

}

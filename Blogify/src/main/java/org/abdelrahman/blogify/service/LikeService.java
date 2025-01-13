package org.abdelrahman.blogify.service;


import org.abdelrahman.blogify.entity.Post;
import org.abdelrahman.blogify.entity.PostLike;
import org.abdelrahman.blogify.entity.User;
import org.abdelrahman.blogify.models.PostLikeDTO;
import org.abdelrahman.blogify.repository.PostLikeRepository;
import org.abdelrahman.blogify.repository.PostRepository;
import org.abdelrahman.blogify.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LikeService  {
    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Autowired
    public LikeService(PostLikeRepository postLikeRepository, PostRepository postRepository, UserRepository userRepository) {
        this.postLikeRepository = postLikeRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public PostLikeDTO addLike(Long postId, Long userId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        PostLike postLike  = new PostLike();
        postLike.setPost(post);
        postLike.setUser(user);
        postLikeRepository.save(postLike);
        return PostLikeDTO.convertToDTO(postLike);
    }

    public void removeLike(Long likeId) {
        PostLike like = postLikeRepository.findById(likeId).orElseThrow(() -> new RuntimeException("Post not found"));
        postLikeRepository.delete(like);
    }

    public List<PostLikeDTO> getLikesByPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        return postLikeRepository.getLikesByPostId(postId);
    }
}

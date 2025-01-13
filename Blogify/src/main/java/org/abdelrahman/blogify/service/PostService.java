package org.abdelrahman.blogify.service;

import org.abdelrahman.blogify.entity.Post;
import org.abdelrahman.blogify.entity.User;
import org.abdelrahman.blogify.exception.UserNotFoundException;
import org.abdelrahman.blogify.models.PostSummaryDTO;
import org.abdelrahman.blogify.models.UserIdPostId;
import org.abdelrahman.blogify.repository.PostRepository;
import org.abdelrahman.blogify.repository.UserRepository;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;


import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public Post createPost(Post post, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User Not Found"));
        post.setAuthor(user);
        return postRepository.save(post);
    }


    public PostSummaryDTO getPostById(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new UserNotFoundException("Post with ID " + postId + " does not exist");
        }
        PostSummaryDTO postSummaryDTO = postRepository.findPostById(postId);
        postSummaryDTO.setCommentsIds(getCommentsIdsByPostId(postId));
        postSummaryDTO.setTagsIds(getTagsIdsByPostIds(postId));
        postSummaryDTO.setCategoriesIds(getCategoriesIdsByPostIds(postId));
        postSummaryDTO.setLikesIds(getLikesIdsByPostIds(postId));
        return postSummaryDTO;
    }
    public Post getAllPostInfoById(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new UserNotFoundException("Post with ID " + postId + " does not exist");
        }
        return postRepository.findAllPostInfoById(postId);
    }

    public List<UserIdPostId> getUserIdAndPostId() {
        return postRepository.findUserIdAndPostId();
    }

    public Post UpdatePost(Post updatedPost) {
        if (!postRepository.existsById(updatedPost.getId())) {
            throw new UserNotFoundException("Post with ID " + updatedPost.getId() + " does not exist");
        }
        return postRepository.save(updatedPost);
    }

    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }

    public Page<PostSummaryDTO> getAllPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PostSummaryDTO> postsPage = postRepository.findAllPosts(pageable);
        List<Long> postIds = postsPage.getContent().stream().map(PostSummaryDTO::getId).collect(Collectors.toList());

        List<Object[]> commentResults = postRepository.findCommentIdsByPostIds(postIds);
        List<Object[]> likeResults = postRepository.findLikeIdsByPostIds(postIds);
        List<Object[]> tagResults = postRepository.findTagIdsByPostIds(postIds);
        List<Object[]> categoryResults = postRepository.findCategoryIdsByPostIds(postIds);

        Map<Long, List<Long>> commentMap = commentResults.stream().collect(Collectors.groupingBy(
                row -> ((Number) row[0]).longValue(),
                Collectors.mapping(row -> ((Number) row[1]).longValue(), Collectors.toList())
        ));
        Map<Long, List<Long>> likeMap = likeResults.stream().collect(Collectors.groupingBy(
                row -> ((Number) row[0]).longValue(),
                Collectors.mapping(row -> ((Number) row[1]).longValue(), Collectors.toList())
        ));
        Map<Long, List<Long>> tagMap = tagResults.stream().collect(Collectors.groupingBy(
                row -> ((Number) row[0]).longValue(),
                Collectors.mapping(row -> ((Number) row[1]).longValue(), Collectors.toList())
        ));
        Map<Long, List<Long>> categoryMap = categoryResults.stream().collect(Collectors.groupingBy(
                row -> ((Number) row[0]).longValue(),
                Collectors.mapping(row -> ((Number) row[1]).longValue(), Collectors.toList())
        ));

        postsPage.forEach(post -> {
            post.setCommentsIds(commentMap.getOrDefault(post.getId(), new ArrayList<>()));
            post.setLikesIds(likeMap.getOrDefault(post.getId(), new ArrayList<>()));
            post.setTagsIds(tagMap.getOrDefault(post.getId(), new ArrayList<>()));
            post.setCategoriesIds(categoryMap.getOrDefault(post.getId(), new ArrayList<>()));
        });

        return postsPage;
    }


    public List<Long> getPostsIdsByUserId(Long userId) {

        return postRepository.getPostsIdsByUserId(userId);
    }

    public HashMap<Long, List<Long>> groupPostIdsByUser() {
        List<UserIdPostId> userIdAndPostId = postRepository.findUserIdAndPostId();
        HashMap<Long, List<Long>> userPostsMap = new HashMap<>();
        userIdAndPostId.forEach(up -> {
            Long userId = up.getUserId();
            Long postId = up.getPostId();
            userPostsMap.computeIfAbsent(userId, k -> new ArrayList<>()).add(postId);
        });
        return userPostsMap;
    }

    private List<Long> getCommentsIdsByPostId(Long postId) {
        return postRepository.findCommentsIdsByPostId(postId);
    }

    private List<Long> getTagsIdsByPostIds(Long postId) {
        return postRepository.findTagsIdsByPostId(postId);
    }

    private List<Long> getLikesIdsByPostIds(Long postId) {
        return postRepository.findLikesIdsByPostId(postId);
    }

    private List<Long> getCategoriesIdsByPostIds(Long postId) {
        return postRepository.findCategoriesIdsByPostId(postId);
    }

}

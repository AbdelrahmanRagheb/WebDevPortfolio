package org.abdelrahman.blogify.rest;

import jakarta.validation.Valid;
import org.abdelrahman.blogify.entity.Post;
import org.abdelrahman.blogify.models.PostSummaryDTO;
import org.abdelrahman.blogify.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.EntityModel;


@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;
    private final PagedResourcesAssembler<PostSummaryDTO> pagedResourcesAssembler;

    @Autowired
    public PostController(PostService postService, PagedResourcesAssembler<PostSummaryDTO> pagedResourcesAssembler) {
        this.postService = postService;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<PostSummaryDTO>>> getAllPosts(@RequestParam(defaultValue = "0") int page,
                                                                               @RequestParam(defaultValue = "10") int size) {
        Page<PostSummaryDTO> postsPage = postService.getAllPosts(page, size);
        PagedModel<EntityModel<PostSummaryDTO>> pagedModel = pagedResourcesAssembler.toModel(postsPage);
        return ResponseEntity.ok(pagedModel);
    }


    @GetMapping("/{postId}")
    public ResponseEntity<PostSummaryDTO> getPostById(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPostById(postId));
    }

    @PostMapping
    public ResponseEntity<Post> createPost(@Valid @RequestBody Post post) {
        Post newPost = postService.createPost(post, post.getAuthor().getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(newPost);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<Post> updatePost(@PathVariable Long postId, @Valid @RequestBody Post updatedPost) {
        updatedPost.setId(postId);
        Post savedPost = postService.UpdatePost(updatedPost);
        return ResponseEntity.ok(savedPost);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        String message = "Post with ID " + postId + " deleted successfully";
        return ResponseEntity.ok(message);
    }


}

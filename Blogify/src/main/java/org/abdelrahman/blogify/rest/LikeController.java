package org.abdelrahman.blogify.rest;


import org.abdelrahman.blogify.models.PostLikeDTO;
import org.abdelrahman.blogify.repository.PostLikeRepository;
import org.abdelrahman.blogify.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class LikeController {
    private final LikeService likeService;


    @Autowired
    public LikeController(LikeService likeService ) {
        this.likeService = likeService;
    }

    @PostMapping("/{postId}/likes")
    public ResponseEntity<PostLikeDTO> addLike(@PathVariable Long postId, @RequestParam Long userId) {
        PostLikeDTO postLikeDTO = likeService.addLike(postId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(postLikeDTO);
    }

    @DeleteMapping("/likes/{likeId}")
    public ResponseEntity<Void> removeLike(@PathVariable Long likeId) {
        likeService.removeLike(likeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{postId}/likes")
    public ResponseEntity<List<PostLikeDTO>> getLikesByPost(@PathVariable Long postId) {
        List<PostLikeDTO> likes = likeService.getLikesByPost(postId);
        return ResponseEntity.ok(likes);
    }
}

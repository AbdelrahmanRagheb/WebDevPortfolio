package org.abdelrahman.blogify.rest;

import org.abdelrahman.blogify.entity.Tag;
import org.abdelrahman.blogify.models.TagDTO;
import org.abdelrahman.blogify.service.TagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping()
    public ResponseEntity<List<TagDTO>> getAllTagsInfo() {
        return ResponseEntity.ok(tagService.getAllTagsInfo());
    }

    @GetMapping("/{tagId}")
    public ResponseEntity<TagDTO> getTagById(@PathVariable Long tagId) {
        return ResponseEntity.ok(tagService.getTagById(tagId));
    }
    @PostMapping
    public ResponseEntity<TagDTO> AddTag(@RequestBody Tag tag) {
        return ResponseEntity.ok(  tagService.addNewTag(tag));
    }

    @DeleteMapping("/{tagId}")
    public ResponseEntity<String> deleteTag(@PathVariable Long tagId) {
        tagService.deleteTag(tagId);
        return ResponseEntity.ok("tag deleted successfully");
    }

}

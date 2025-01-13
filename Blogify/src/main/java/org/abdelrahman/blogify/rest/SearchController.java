package org.abdelrahman.blogify.rest;

import org.abdelrahman.blogify.models.*;
import org.abdelrahman.blogify.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;
    private final PagedResourcesAssembler<PostSearchResultDTO> postPagedResourcesAssembler;
    private final PagedResourcesAssembler<UserSearchResultDTO> userPagedResourcesAssembler;
    private final PagedResourcesAssembler<TagSearchResultDTO> tagPagedResourcesAssembler;
    private final PagedResourcesAssembler<CategorySearchResultDTO> categoryPagedResourcesAssembler;

    @Autowired
    public SearchController(SearchService searchService,
                            PagedResourcesAssembler<PostSearchResultDTO> postPagedResourcesAssembler,
                            PagedResourcesAssembler<UserSearchResultDTO> userPagedResourcesAssembler,
                            PagedResourcesAssembler<TagSearchResultDTO> tagPagedResourcesAssembler,
                            PagedResourcesAssembler<CategorySearchResultDTO> categoryPagedResourcesAssembler) {
        this.searchService = searchService;
        this.postPagedResourcesAssembler = postPagedResourcesAssembler;
        this.userPagedResourcesAssembler = userPagedResourcesAssembler;
        this.tagPagedResourcesAssembler=tagPagedResourcesAssembler;
        this.categoryPagedResourcesAssembler=categoryPagedResourcesAssembler;
    }

    @GetMapping("/posts")
    public ResponseEntity<PagedModel<EntityModel<PostSearchResultDTO>>> searchPosts(@RequestParam String query, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<PostSearchResultDTO> result = searchService.searchPosts(query, page, size);
        PagedModel<EntityModel<PostSearchResultDTO>> pagedModel = postPagedResourcesAssembler.toModel(result);
        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/users")
    public ResponseEntity<PagedModel<EntityModel<UserSearchResultDTO>>> searchUsers(@RequestParam String query, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<UserSearchResultDTO> result = searchService.searchUsers(query, page, size);
        PagedModel<EntityModel<UserSearchResultDTO>> pagedModel = userPagedResourcesAssembler.toModel(result);
        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/tags")
    public ResponseEntity<PagedModel<EntityModel<TagSearchResultDTO>>> searchTags(@RequestParam String query, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<TagSearchResultDTO> tagSearchResultDTOS = searchService.searchTags(query, page, size);
        PagedModel<EntityModel<TagSearchResultDTO>> pagedModel = tagPagedResourcesAssembler.toModel(tagSearchResultDTOS);
        return ResponseEntity.ok(pagedModel);
    }
    @GetMapping("/categories")
    public ResponseEntity<PagedModel<EntityModel<CategorySearchResultDTO>>> searchCategories(@RequestParam String query, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<CategorySearchResultDTO> categorySearchResultDTOS = searchService.searchCategories(query, page, size);
        PagedModel<EntityModel<CategorySearchResultDTO>> pagedModel = categoryPagedResourcesAssembler.toModel(categorySearchResultDTOS);
        return ResponseEntity.ok(pagedModel);
    }

}

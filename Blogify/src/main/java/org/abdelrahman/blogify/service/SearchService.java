package org.abdelrahman.blogify.service;

import org.abdelrahman.blogify.entity.Category;
import org.abdelrahman.blogify.entity.Post;
import org.abdelrahman.blogify.entity.Tag;
import org.abdelrahman.blogify.entity.User;
import org.abdelrahman.blogify.models.*;
import org.abdelrahman.blogify.repository.CategoryRepository;
import org.abdelrahman.blogify.repository.PostRepository;
import org.abdelrahman.blogify.repository.TagRepository;
import org.abdelrahman.blogify.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private PostRepository postRepository;
    private UserRepository userRepository;
    private TagRepository tagRepository;
    private CategoryRepository categoryRepository;

    public SearchService(PostRepository postRepository, UserRepository userRepository, TagRepository tagRepository, CategoryRepository categoryRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
        this.categoryRepository = categoryRepository;
    }


    public Page<PostSearchResultDTO> searchPosts(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postRepository.searchPosts(query, pageable);
        return posts.map(post -> new PostSearchResultDTO(post.getId(), post.getTitle(), post.getContent(), post.getCreatedAt(), post.getAuthor().getId()));
    }

    public Page<UserSearchResultDTO> searchUsers(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userRepository.searchUsers(query, pageable);
        return users.map(user -> new UserSearchResultDTO(user.getUsername(), user.getBio(), user.getRole(), user.getCreatedAt()));
    }

    public Page<TagSearchResultDTO> searchTags(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        System.out.println("Searching for tags with query: " + query);
        Page<Tag> tags = tagRepository.searchTags(query, pageable);
        if (tags.isEmpty()) {
            System.out.println("No tags found for query: " + query);
            return Page.empty();
        }
        List<Long> tagIds = tags.stream().map(Tag::getId).collect(Collectors.toList());
        System.out.println("Tag IDs found: " + tagIds);
        Page<TagSearchResultDTO> tagDTOs = tagRepository.findAllPostsByTagIds(tagIds, pageable);
        System.out.println("Tag and post associations found: " + tagDTOs.getContent());
        tagDTOs.forEach(tagDTO -> {
            List<Long> postsIdsWithTag = tagDTOs.getContent().stream().filter(dto -> dto.getName().equals(tagDTO.getName())).map(TagSearchResultDTO::getPostId).collect(Collectors.toList());
            tagDTO.setPostsIdsWithTag(postsIdsWithTag);
        });
        System.out.println("Tags and associated posts found: " + tagDTOs.getContent());
        return tagDTOs;
    }

    public Page<CategorySearchResultDTO> searchCategories(String query, int page, int size) {
        // Step 1: Retrieve paginated categories based on the search query
        Pageable pageable = PageRequest.of(page, size);
        System.out.println("Searching for category with query: " + query);
        Page<Category> categoryDTOS = categoryRepository.searchCategories(query, pageable);

        // Step 2: Collect all category IDs
        List<Long> categoryIds = categoryDTOS.stream()
                .map(Category::getId)
                .collect(Collectors.toList());

        if (categoryIds.isEmpty()) {
            System.out.println("No category found for query: " + query);

            // Return an empty Page object to maintain pagination structure
            return Page.empty();
        }else  System.out.println("category IDs found: " + categoryIds);


        // Step 3: Retrieve all post IDs for the collected category IDs
        Page<Object[]> postIdResults = categoryRepository.findPostIdsByCategoryIds(categoryIds, pageable);

        // Step 4: Group post IDs by category ID
        Map<Long, List<Long>> categoryPostsMap = postIdResults.stream()
                .collect(Collectors.groupingBy(
                        result -> (Long) result[0], // Group by category ID
                        Collectors.mapping(result -> (Long) result[1], Collectors.toList()) // Map post IDs
                ));

        // Step 5: Build CategorySearchResultDTO list
        List<CategorySearchResultDTO> searchResults = categoryDTOS.stream()
                .map(category -> new CategorySearchResultDTO(
                        category.getName(),
                        categoryPostsMap.getOrDefault(category.getId(), Collections.emptyList())
                ))
                .collect(Collectors.toList());

        // Step 6: Return a paginated result
        return new PageImpl<>(searchResults, pageable, categoryDTOS.getTotalElements());
    }



}

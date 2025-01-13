package org.abdelrahman.blogify.service;

import org.abdelrahman.blogify.entity.Tag;
import org.abdelrahman.blogify.exception.TagException;
import org.abdelrahman.blogify.models.PostTagDTO;
import org.abdelrahman.blogify.models.TagDTO;
import org.abdelrahman.blogify.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TagService {

    private TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public TagDTO addNewTag(Tag tag) {

        if(tagRepository.getTagByName(tag.getName())!=null){
            throw new TagException("TAG Already Exist");
        }

        tagRepository.save(tag);
        return tagRepository.getTagByName(tag.getName());
    }

    public TagDTO getTagById(Long tagId) {
        Tag tag = tagRepository.findById(tagId).orElseThrow(() -> new TagException("Tag Not Found"));
        List<Long> posts = getAllPostsByTagId(tagId);
        return new TagDTO(tag.getId(), tag.getName(), posts);
    }

    private List<Long> getAllPostsByTagId(Long tagId) {
        return tagRepository.findAllPostsByTagId(tagId);
    }

    public List<TagDTO> getAllTagsInfo() {
        List<TagDTO> allTags = tagRepository.findAllTags();
        List<PostTagDTO> allTagsInfo = tagRepository.findAllTagsInfo();
        Map<Long, TagDTO> tagMap = allTags.stream().collect(Collectors.toMap(TagDTO::getTagId, tag -> tag));
        for (PostTagDTO postTagInfo : allTagsInfo) {
            TagDTO tagDTO = tagMap.get(postTagInfo.getTagId());
            if (tagDTO != null) {
                tagDTO.getPostsIdsWithTag().add(postTagInfo.getPostId());
            }
        }
        return allTags;
    }

    public void deleteTag(Long tagId) {
        Tag tag = tagRepository.findById(tagId).orElseThrow(() -> new TagException("TAG Does not Exist"));
        tagRepository.delete(tag);

    }
}


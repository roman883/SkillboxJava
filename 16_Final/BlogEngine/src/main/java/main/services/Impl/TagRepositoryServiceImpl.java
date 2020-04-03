package main.services.Impl;

import main.api.response.ResponseApi;
import main.api.response.ResponseTags;
import main.model.entities.Tag;
import main.model.repositories.TagRepository;
import main.services.interfaces.TagRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TagRepositoryServiceImpl implements TagRepositoryService {

    @Autowired
    private TagRepository tagRepository;

    @Override
    public ResponseEntity<ResponseApi> getTags(String query) {
        if (query == null || query.equals("") || query.isBlank()) {
            return getTagsWithoutQuery();
        } else {
            List<Tag> queriedTags = tagRepository.getAllTagsListByQuerySortedByIdDesc(query);
            return getResponseEntityByTagsList(queriedTags);
        }
    }

    @Override
    public Set<Tag> getAllTags() {
        return new HashSet<>(tagRepository.findAll());
    }

    @Override
    public Tag addTag(Tag tag) {
        if (tag == null) {
            return null;
        } else {
            return tagRepository.save(tag);
        }
    }

    @Override
    public void deleteTag(Tag tag) {
        tagRepository.delete(tag);
    }

    @Override
    public ResponseEntity<ResponseApi> getTagsWithoutQuery() { // Также для получения Map - Тэг, вес можно создать DTO или получать list<object[]> и вручную указывать что значит какой столбец
        List<Tag> allTags = tagRepository.getAllTagsListSortedByIdDesc();
        return getResponseEntityByTagsList(allTags);
    }

    private ResponseEntity<ResponseApi> getResponseEntityByTagsList(List<Tag> allTags) {
        HashMap<String, Double> queryTagsMap = new HashMap<>();
        if (!allTags.isEmpty()) {
            Integer mostFrequentTagCount = tagRepository.getMaxTagCount();
            for (Tag tag : allTags) {
                Double weight = ((double) tagRepository.getTagCountByTagId(tag.getId()) / (double) mostFrequentTagCount);
                queryTagsMap.put(tag.getName(), weight);
            }
            return new ResponseEntity<>(new ResponseTags(queryTagsMap), HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(null); // Тегов нет, запрос - ОК.
        }
    }
}

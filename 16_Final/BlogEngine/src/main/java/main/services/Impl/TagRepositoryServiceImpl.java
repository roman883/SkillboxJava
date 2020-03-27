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

    // TODO Получать все теги и их вес из базы данных? формат (Тег : Вес) Теги "весят" немного, можно и из БД все выгружать
    @Override
    public ResponseEntity<ResponseApi> getTags(String query) {
        HashMap<String, Double> queryTagsMap = new HashMap<>();
        ArrayList<Tag> allTags = new ArrayList<>(tagRepository.findAll());
        if (query == null || query.equals("") || query.isBlank()) {
            return getTagsWithoutQuery();
        } else {
            for (Tag tag : allTags) {
                if (tag.getName().contains(query)) {
                    String tagName = tag.getName();
                    double tempTagCount = queryTagsMap.getOrDefault(tagName, 0.0);
                    queryTagsMap.put(tagName, tempTagCount + 1.0);
                }
            }
        }
        Double mostFrequentTag = Collections.max(queryTagsMap.values());
        for (String key : queryTagsMap.keySet()) {
            Double weight = (queryTagsMap.get(key) / mostFrequentTag);
            queryTagsMap.put(key, weight); // Меняем количество на вес тэга
        }
        return new ResponseEntity<>(new ResponseTags(queryTagsMap), HttpStatus.OK);
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
    public ResponseEntity<ResponseApi> getTagsWithoutQuery() {
        HashMap<String, Double> queryTagsMap = new HashMap<>();
        ArrayList<Tag> allTags = new ArrayList<>(tagRepository.findAll());
        for (Tag tag : allTags) {
            String tagName = tag.getName();
            double tempTagCount = queryTagsMap.getOrDefault(tagName, 0.0);
            queryTagsMap.put(tagName, tempTagCount + 1.0);
        }
        if (!allTags.isEmpty()) {
        Double mostFrequentTag = Collections.max(queryTagsMap.values());
        for (String key : queryTagsMap.keySet()) {
            Double weight = (queryTagsMap.get(key) / mostFrequentTag);
            queryTagsMap.put(key, weight); // Меняем количество на вес тэга
        } }
        return new ResponseEntity<>(new ResponseTags(queryTagsMap), HttpStatus.OK);
    }
}

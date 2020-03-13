package main.services.Impl;

import main.model.entities.Tag;
import main.model.repositories.TagRepository;
import main.services.interfaces.TagRepositoryService;
import org.json.JSONArray;
import org.json.JSONObject;
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
    public ResponseEntity<String> getTags(String query) {
        ArrayList<Tag> allTags = new ArrayList<>();
        HashMap<String, Double> queryTagsMap = new HashMap<>();
        tagRepository.findAll().forEach(allTags::add);
        if (query.equals("")) {
            for (Tag tag : allTags) {
                String tagName = tag.getName();
                double tempTagCount = queryTagsMap.getOrDefault(tagName, 0.0);
                queryTagsMap.put(tagName, tempTagCount + 1.0);
            }
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
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (String key : queryTagsMap.keySet()) {
            JSONObject json = new JSONObject();
            json.put("name", key).put("weight", queryTagsMap.get(key));
            jsonArray.put(json);
        }
        jsonObject.put("tags", jsonArray);
        return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
    }

    @Override
    public Set<Tag> getAllTags() {
        HashSet<Tag> tags = new HashSet<>();
        tagRepository.findAll().forEach(tags::add);
        return tags;
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
}

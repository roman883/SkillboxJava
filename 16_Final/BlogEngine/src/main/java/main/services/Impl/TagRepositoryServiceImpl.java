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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class TagRepositoryServiceImpl implements TagRepositoryService {

    @Autowired
    private TagRepository tagRepository;

    @Override
    public ResponseEntity<String> getTags(String query) {
        ArrayList<Tag> allTags = new ArrayList<>();
        HashMap<String, Integer> queryTagsMap = new HashMap<>();
        tagRepository.findAll().forEach(allTags::add);
        if (query.equals("")) {
            for (Tag t : allTags) {
                String tag = t.getName();
                int tempTagCount = queryTagsMap.getOrDefault(tag, 0);
                queryTagsMap.put(tag, tempTagCount + 1);
            }
        } else {
            for (Tag t : allTags) {
                if (t.getName().contains(query)) {
                    String tag = t.getName();
                    int tempTagCount = queryTagsMap.getOrDefault(tag, 0);
                    queryTagsMap.put(tag, tempTagCount + 1);
                }
            }
        }
        int mostFrequentTag = Collections.max(queryTagsMap.values());
        for (String key : queryTagsMap.keySet()) {
            Integer weight = queryTagsMap.get(key) / mostFrequentTag;
            queryTagsMap.put(key, weight); // Меняем количество на вес тэга
        }
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (String key : queryTagsMap.keySet()) {
            JSONObject json = new JSONObject();
            json.put("name", key).put("weight", queryTagsMap.get(key));
            jsonArray.put(json);
        }
        jsonObject.put("`tags`", jsonArray);
        return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
    }
}

package main.services.interfaces;

import main.model.entities.Tag;
import org.springframework.http.ResponseEntity;

import java.util.Set;

public interface TagRepositoryService {

    ResponseEntity<String> getTags(String query);

    Set<Tag> getAllTags();

    Tag addTag(Tag tag);

    void deleteTag(Tag tag);
}

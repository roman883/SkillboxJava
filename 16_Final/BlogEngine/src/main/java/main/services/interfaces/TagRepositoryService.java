package main.services.interfaces;

import main.model.entities.Tag;
import main.model.responses.ResponseAPI;
import org.springframework.http.ResponseEntity;

import java.util.Set;

public interface TagRepositoryService {

    ResponseEntity<ResponseAPI> getTags(String query);

    Set<Tag> getAllTags();

    Tag addTag(Tag tag);

    void deleteTag(Tag tag);

    ResponseEntity<ResponseAPI> getTagsWithoutQuery();
}

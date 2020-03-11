package main.services.interfaces;

import org.springframework.http.ResponseEntity;

public interface TagRepositoryService {

    ResponseEntity<String> getTags(String query);
}

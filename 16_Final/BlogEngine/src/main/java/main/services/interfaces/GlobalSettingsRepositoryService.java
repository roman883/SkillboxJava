package main.services.interfaces;

import main.model.entities.GlobalSettings;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpSession;
import java.util.HashSet;

public interface GlobalSettingsRepositoryService {

    ResponseEntity<?> getGlobalSettings(HttpSession session);

    ResponseEntity<?> setGlobalSettings(Boolean multiUserMode, Boolean postPremoderation, Boolean statisticsIsPublic,
                                     HttpSession session);

    HashSet<GlobalSettings> getAllGlobalSettingsSet();
}

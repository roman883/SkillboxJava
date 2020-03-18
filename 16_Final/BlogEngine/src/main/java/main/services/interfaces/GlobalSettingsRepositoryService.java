package main.services.interfaces;

import main.api.request.SetGlobalSettingsRequest;
import main.model.entities.GlobalSettings;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpSession;
import java.util.HashSet;

public interface GlobalSettingsRepositoryService {

    ResponseEntity<?> getGlobalSettings(HttpSession session);

    ResponseEntity<?> setGlobalSettings(SetGlobalSettingsRequest setGlobalSettingsRequest,
                                        HttpSession session);

    HashSet<GlobalSettings> getAllGlobalSettingsSet();
}

package main.services.Impl;

import main.model.entities.GlobalSettings;
import main.model.entities.User;
import main.model.repositories.GlobalSettingsRepository;
import main.services.interfaces.GlobalSettingsRepositoryService;
import main.services.interfaces.UserRepositoryService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.HashSet;

@Service
public class GlobalSettingsRepositoryServiceImpl implements GlobalSettingsRepositoryService {

    @Autowired
    private GlobalSettingsRepository globalSettingsRepository;

    @Override
    public ResponseEntity<String> getGlobalSettings(HttpSession session, UserRepositoryService userRepositoryService) {
        Integer userId = userRepositoryService.getUserIdBySession(session);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        User user = userRepositoryService.getUser(userId).getBody();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Ошибка, пользователь не найден, а сессия есть
        }
        if (!user.isModerator()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // Недостаточно прав
        }
        HashSet<GlobalSettings> settings = getAllGlobalSettingsSet();
        JSONObject res = new JSONObject();
        for (GlobalSettings g : settings) {
            boolean currentSetting = false;
            if (g.getValue().toUpperCase().equals("YES")) {
                currentSetting = true;
            }
            res.put(g.getName(), currentSetting);
        }
        return new ResponseEntity<String>(res.toString(), HttpStatus.OK);

    }

    @Override
    public ResponseEntity<String> setGlobalSettings(Boolean multiUserMode, Boolean postPremoderation, Boolean statisticsIsPublic,
                                            HttpSession session, UserRepositoryService userRepositoryService) {
        if (multiUserMode == null || postPremoderation == null || statisticsIsPublic == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Ошибка в параметрах
        }
        Integer userId = userRepositoryService.getUserIdBySession(session);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        User user = userRepositoryService.getUser(userId).getBody();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Ошибка, пользователь не найден, а сессия есть
        }
        if (!user.isModerator()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // Недостаточно прав
        }
        HashSet<GlobalSettings> settings = getAllGlobalSettingsSet();
        JSONObject res = new JSONObject();
        for (GlobalSettings g : settings) {
            String settingName = g.getName().toUpperCase();
            switch (settingName) {
                case "MULTIUSER_MODE": {
                    String value = booleanToYesOrNo(multiUserMode);
                    g.setValue(value);
                    res.put("MULTIUSER_MODE", multiUserMode);
                    break;
                }
                case "POST_PREMODERATION": {
                    String value = booleanToYesOrNo(postPremoderation);
                    g.setValue(value);
                    res.put("POST_PREMODERATION", postPremoderation);
                    break;
                }
                case "STATISTICS_IS_PUBLIC": {
                    String value = booleanToYesOrNo(statisticsIsPublic);
                    g.setValue(value);
                    res.put("STATISTICS_IS_PUBLIC", statisticsIsPublic);
                    break;
                }
            }
        }
        return new ResponseEntity<String>(res.toString(), HttpStatus.OK);
    }

    @Override
    public HashSet<GlobalSettings> getAllGlobalSettingsSet() {
        HashSet<GlobalSettings> gsSet = new HashSet<>();
        globalSettingsRepository.findAll().forEach(gsSet::add);
        return gsSet;
    }

    private Boolean yesOrNoToBoolean(String yesOrNo) {
        if (yesOrNo.toUpperCase().equals("YES")) {
            return true;
        } else if (yesOrNo.toUpperCase().equals("NO")) {
            return false;
        } else return null;
    }

    private String booleanToYesOrNo(boolean bool) {
        if (bool) {
            return "YES";
        } else return "NO";
    }
}

package main.services.Impl;

import main.model.entities.GlobalSettings;
import main.model.entities.User;
import main.model.repositories.GlobalSettingsRepository;
import main.model.responses.ResponseAPI;
import main.model.responses.ResponseSettings;
import main.services.interfaces.GlobalSettingsRepositoryService;
import main.services.interfaces.UserRepositoryService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.HashSet;

@Service
public class GlobalSettingsRepositoryServiceImpl implements GlobalSettingsRepositoryService {

    @Autowired
    private GlobalSettingsRepository globalSettingsRepository;
    @Autowired
    private UserRepositoryService userRepositoryService;

    @Override
    public ResponseEntity<?> getGlobalSettings(HttpSession session) {
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
        ResponseSettings responseSettings = new ResponseSettings(getAllGlobalSettingsSet());
        return new ResponseEntity<>(responseSettings.getMap(), HttpStatus.OK);

    }

    @Override
    public ResponseEntity<?> setGlobalSettings(Boolean multiUserMode, Boolean postPremoderation, Boolean statisticsIsPublic,
                                            HttpSession session) {
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
        HashSet<GlobalSettings> resultSet = new HashSet<>();
        for (GlobalSettings g : settings) {
            String settingName = g.getName().toUpperCase();
            switch (settingName) {
                case "MULTIUSER_MODE": {
                    String value = booleanToYesOrNo(multiUserMode);
                    g.setValue(value);
                    globalSettingsRepository.save(g);
                    resultSet.add(g);
//                    result.put("MULTIUSER_MODE", multiUserMode);
                    break;
                }
                case "POST_PREMODERATION": {
                    String value = booleanToYesOrNo(postPremoderation);
                    g.setValue(value);
                    globalSettingsRepository.save(g);
                    resultSet.add(g);
//                    result.put("POST_PREMODERATION", postPremoderation);
                    break;
                }
                case "STATISTICS_IS_PUBLIC": {
                    String value = booleanToYesOrNo(statisticsIsPublic);
                    g.setValue(value);
                    globalSettingsRepository.save(g);
                    resultSet.add(g);
//                    result.put("STATISTICS_IS_PUBLIC", statisticsIsPublic);
                    break;
                }
            }
        }
        return new ResponseEntity<>(new ResponseSettings(resultSet).getMap(), HttpStatus.OK);
    }

    @Override
    public HashSet<GlobalSettings> getAllGlobalSettingsSet() {
        HashSet<GlobalSettings> gsSet = new HashSet<>();
        globalSettingsRepository.findAll().forEach(gsSet::add);
        return gsSet;
    }

//    private Boolean yesOrNoToBoolean(String yesOrNo) {
//        if (yesOrNo.toUpperCase().equals("YES")) {
//            return true;
//        } else if (yesOrNo.toUpperCase().equals("NO")) {
//            return false;
//        } else return null;
//    }

    private String booleanToYesOrNo(boolean bool) {
        if (bool) {
            return "YES";
        } else return "NO";
    }
}

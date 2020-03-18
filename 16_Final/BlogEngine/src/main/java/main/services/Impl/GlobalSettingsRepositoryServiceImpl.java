package main.services.Impl;

import main.api.request.SetGlobalSettingsRequest;
import main.model.entities.GlobalSettings;
import main.model.entities.User;
import main.model.repositories.GlobalSettingsRepository;
import main.api.response.*;
import main.services.interfaces.GlobalSettingsRepositoryService;
import main.services.interfaces.UserRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

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
        return new ResponseEntity<>(responseSettings, HttpStatus.OK);

    }

    @Override
    public ResponseEntity<?> setGlobalSettings(SetGlobalSettingsRequest setGlobalSettingsRequest, //TODO пока не работает,
    // не создается объект Request с параметрами (их может быть 1-2 или 3, ожидаю 3)
                                               HttpSession session) {
        Boolean multiUserMode = setGlobalSettingsRequest.getMULTIUSER_MODE();
        Boolean postPremoderation = setGlobalSettingsRequest.getPOST_PREMODERATION();
        Boolean statisticsIsPublic = setGlobalSettingsRequest.getSTATISTICS_IS_PUBLIC();
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
        Map<String, Boolean> resultMap = new HashMap<>();
        for (GlobalSettings g : settings) {
            String settingCode = g.getCode().toUpperCase();
            switch (settingCode) {
                case "MULTIUSER_MODE": {
                    String value = booleanToYesOrNo(multiUserMode);
                    g.setValue(value);
                    globalSettingsRepository.save(g);
                    resultMap.put("MULTIUSER_MODE", multiUserMode);
                    break;
                }
                case "POST_PREMODERATION": {
                    String value = booleanToYesOrNo(postPremoderation);
                    g.setValue(value);
                    globalSettingsRepository.save(g);
                    resultMap.put("POST_PREMODERATION", postPremoderation);
//                    result.put("POST_PREMODERATION", postPremoderation);
                    break;
                }
                case "STATISTICS_IS_PUBLIC": {
                    String value = booleanToYesOrNo(statisticsIsPublic);
                    g.setValue(value);
                    globalSettingsRepository.save(g);
                    resultMap.put("STATISTICS_IS_PUBLIC", statisticsIsPublic);
//                    result.put("STATISTICS_IS_PUBLIC", statisticsIsPublic);
                    break;
                }
            }
        }
        return new ResponseEntity<>(new ResponseSettings(resultMap), HttpStatus.OK);
    }

    @Override
    public HashSet<GlobalSettings> getAllGlobalSettingsSet() {
        HashSet<GlobalSettings> gsSet = new HashSet<>();
        globalSettingsRepository.findAll().forEach(gsSet::add);
        // Если нет каких-то настроек, устанавливаем значения по умолчанию false
        if (gsSet.isEmpty()) {
            GlobalSettings globalSettingsMultiUser =
                    new GlobalSettings("MULTIUSER_MODE", "Многопользовательский режим", "NO");
            GlobalSettings globalSettingsPreModeration =
                    new GlobalSettings("POST_PREMODERATION", "Премодерация постов", "NO");
            GlobalSettings globalSettingsStatisticsIsPublic =
                    new GlobalSettings("STATISTICS_IS_PUBLIC", "Показывать всем статистику блога", "NO");
            gsSet.add(globalSettingsRepository.save(globalSettingsMultiUser));
            gsSet.add(globalSettingsRepository.save(globalSettingsPreModeration));
            gsSet.add(globalSettingsRepository.save(globalSettingsStatisticsIsPublic));
        } else {
            boolean hasMultiuserMode = false;
            boolean hasPostPremoderation = false;
            boolean hasStatisticsIsPublic = false;
            for (GlobalSettings g : gsSet) {
                String globalSettingsCode = g.getCode();
                switch (globalSettingsCode) {
                    case ("MULTIUSER_MODE"):
                        hasMultiuserMode = true;
                        break;
                    case ("POST_PREMODERATION"):
                        hasPostPremoderation = true;
                        break;
                    case ("STATISTICS_IS_PUBLIC"):
                        hasStatisticsIsPublic = true;
                        break;
                }
            }
            if (!hasMultiuserMode) {
                GlobalSettings globalSettings =
                        new GlobalSettings("MULTIUSER_MODE", "Многопользовательский режим", "NO");
                gsSet.add(globalSettingsRepository.save(globalSettings));
            }
            if (!hasPostPremoderation) {
                GlobalSettings globalSettings =
                        new GlobalSettings("POST_PREMODERATION", "Премодерация постов", "NO");
                gsSet.add(globalSettingsRepository.save(globalSettings));
            }
            if (!hasStatisticsIsPublic) {
                GlobalSettings globalSettings =
                        new GlobalSettings("STATISTICS_IS_PUBLIC", "Показывать всем статистику блога", "NO");
                gsSet.add(globalSettingsRepository.save(globalSettings));
            }
        }
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

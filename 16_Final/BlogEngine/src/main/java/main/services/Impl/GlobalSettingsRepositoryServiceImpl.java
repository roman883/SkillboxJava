package main.services.Impl;

import lombok.extern.slf4j.Slf4j;
import main.api.request.SetGlobalSettingsRequest;
import main.api.response.ResponseApi;
import main.api.response.ResponseBadReqMsg;
import main.api.response.ResponseSettings;
import main.model.entities.GlobalSettings;
import main.model.entities.User;
import main.model.repositories.GlobalSettingsRepository;
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

@Slf4j
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
            log.warn("--- Не найден пользователь по номеру сессии: " + session.getId());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        User user = userRepositoryService.getUser(userId).getBody();
        if (user == null) {
            log.warn("--- Не найден пользователь по userID: " + userId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Ошибка, пользователь не найден, а сессия есть
        }
        if (!user.isModerator()) {
            log.info("--- Для данного действия пользователю " + user.getId() + ":" + user.getName() + " требуются права модератора");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // Недостаточно прав
        }
        ResponseSettings responseSettings = new ResponseSettings(getAllGlobalSettingsSet());
        ResponseEntity<ResponseApi> response = new ResponseEntity<>(responseSettings, HttpStatus.OK);
        log.info("--- Направляется ответ: {" + "HttpStatus:" + response.getStatusCode() + "," + response.getBody() + "}");
        return response;

    }

    @Override
    public ResponseEntity<?> setGlobalSettings(SetGlobalSettingsRequest setGlobalSettingsRequest,
                                               HttpSession session) {
        Boolean multiUserModeSetting = setGlobalSettingsRequest.getMultiuserMode();
        Boolean postPremoderationSetting = setGlobalSettingsRequest.getPostPremoderation();
        Boolean statisticsIsPublicSetting = setGlobalSettingsRequest.getStatisticsIsPublic();
        // Проверка: заданы ли какие-то параметры
        if (multiUserModeSetting == null && postPremoderationSetting == null && statisticsIsPublicSetting == null) {
            ResponseEntity<ResponseApi> response = new ResponseEntity<>(
                    new ResponseBadReqMsg("Не переданы параметры настроек"), HttpStatus.BAD_REQUEST);
            log.warn("--- Не заданы параметры настроек");
            return response;
        }
        // Проверка: есть ли пользователь и права на внесение изменений в настройки
        Integer userId = userRepositoryService.getUserIdBySession(session);
        if (userId == null) {
            log.warn("--- Не найден пользователь по номеру сессии: " + session.getId());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        User user = userRepositoryService.getUser(userId).getBody();
        if (user == null) {
            log.warn("--- Не найден пользователь по userID: " + userId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Ошибка, пользователь не найден, а сессия есть
        }
        if (!user.isModerator()) {
            log.info("--- Для данного действия пользователю " + user.getId() + ":" + user.getName() + " требуются права модератора");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // Недостаточно прав
        }
        // Устанавливаем новые настройки и получаем результат
        HashSet<GlobalSettings> settings = getAllGlobalSettingsSet();
        Map<String, Boolean> resultMap = new HashMap<>();
        // для текущих настроек проверяем их наличие и устанавливаем новые
        for (GlobalSettings g : settings) {
            String settingCode = g.getCode().toUpperCase();
            switch (settingCode) {
                case MULTIUSER_MODE: {
                    if (multiUserModeSetting != null) {
                        g.setValue(convertBooleanToYesOrNo(multiUserModeSetting));
                        globalSettingsRepository.save(g);
                        log.info("--- Установлена настройка " + MULTIUSER_MODE + " со значением " + g.getValue());
                        resultMap.put(MULTIUSER_MODE, multiUserModeSetting);
                    } else {
                        resultMap.put(MULTIUSER_MODE, yesOrNoToBoolean(g.getValue()));
                    }
                    break;
                }
                case POST_PREMODERATION: {
                    if (postPremoderationSetting != null) {
                        g.setValue(convertBooleanToYesOrNo(postPremoderationSetting));
                        globalSettingsRepository.save(g);
                        log.info("--- Установлена настройка " + POST_PREMODERATION + " со значением " + g.getValue());
                        resultMap.put(POST_PREMODERATION, postPremoderationSetting);
                    } else {
                        resultMap.put(POST_PREMODERATION, yesOrNoToBoolean(g.getValue()));
                    }
                    break;
                }
                case STATISTICS_IS_PUBLIC: {
                    if (statisticsIsPublicSetting != null) {
                        g.setValue(convertBooleanToYesOrNo(statisticsIsPublicSetting));
                        globalSettingsRepository.save(g);
                        log.info("--- Установлена настройка " + STATISTICS_IS_PUBLIC + " со значением " + g.getValue());
                        resultMap.put(STATISTICS_IS_PUBLIC, statisticsIsPublicSetting);
                    } else {
                        resultMap.put(STATISTICS_IS_PUBLIC, yesOrNoToBoolean(g.getValue()));
                    }
                    break;
                }
            }
        }
        ResponseEntity<ResponseApi> response = new ResponseEntity<>(new ResponseSettings(resultMap), HttpStatus.OK);
        log.info("--- Направляется ответ: {" + "HttpStatus:" + response.getStatusCode() + "," + response.getBody() + "}");
        return response;
    }

    @Override
    public HashSet<GlobalSettings> getAllGlobalSettingsSet() {
        HashSet<GlobalSettings> gsSet = new HashSet<>(globalSettingsRepository.findAll());
        // Если нет каких-то или всех настроек, устанавливаем значения по умолчанию false
        if (gsSet.isEmpty()) {
            log.info("--- Отсутствуют данные по глобальным настройкам. Устанавливаются значения по-умолчанию");
            GlobalSettings globalSettingsMultiUser =
                    new GlobalSettings(MULTIUSER_MODE, MULTIUSER_MODE_NAME, "NO");
            GlobalSettings globalSettingsPreModeration =
                    new GlobalSettings(POST_PREMODERATION, POST_PREMODERATION_NAME, "NO");
            GlobalSettings globalSettingsStatisticsIsPublic =
                    new GlobalSettings(STATISTICS_IS_PUBLIC, STATISTICS_IS_PUBLIC_NAME, "NO");
            gsSet.add(globalSettingsRepository.save(globalSettingsMultiUser));
            log.info("--- Установлена глобальная настрока: " + globalSettingsMultiUser);
            gsSet.add(globalSettingsRepository.save(globalSettingsPreModeration));
            log.info("--- Установлена глобальная настрока: " + globalSettingsPreModeration);
            gsSet.add(globalSettingsRepository.save(globalSettingsStatisticsIsPublic));
            log.info("--- Установлена глобальная настрока: " + globalSettingsStatisticsIsPublic);
        } else {
            boolean hasMultiuserMode = false;
            boolean hasPostPremoderation = false;
            boolean hasStatisticsIsPublic = false;
            for (GlobalSettings g : gsSet) {
                String globalSettingsCode = g.getCode().toUpperCase();
                switch (globalSettingsCode) {
                    case (MULTIUSER_MODE):
                        hasMultiuserMode = true;
                        break;
                    case (POST_PREMODERATION):
                        hasPostPremoderation = true;
                        break;
                    case (STATISTICS_IS_PUBLIC):
                        hasStatisticsIsPublic = true;
                        break;
                }
            }
            if (!hasMultiuserMode) {
                GlobalSettings globalSettings =
                        new GlobalSettings(MULTIUSER_MODE, MULTIUSER_MODE_NAME, "NO");
                gsSet.add(globalSettingsRepository.save(globalSettings));
            }
            if (!hasPostPremoderation) {
                GlobalSettings globalSettings =
                        new GlobalSettings(POST_PREMODERATION, POST_PREMODERATION_NAME, "NO");
                gsSet.add(globalSettingsRepository.save(globalSettings));
            }
            if (!hasStatisticsIsPublic) {
                GlobalSettings globalSettings =
                        new GlobalSettings(STATISTICS_IS_PUBLIC, STATISTICS_IS_PUBLIC_NAME, "NO");
                gsSet.add(globalSettingsRepository.save(globalSettings));
            }
        }
        log.info("--- Возвращен список настроек: " + gsSet.toString());
        return gsSet;
    }

    private String convertBooleanToYesOrNo(boolean bool) {
        if (bool) {
            return "YES";
        } else return "NO";
    }

    private Boolean yesOrNoToBoolean(String yesOrNo) {
        if (yesOrNo.toUpperCase().equals("YES")) {
            return true;
        } else if (yesOrNo.toUpperCase().equals("NO")) {
            return false;
        } else return null;
    }
}

package main.api.response;

import main.model.entities.GlobalSettings;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ResponseSettings implements ResponseApi {

    private Boolean MULTIUSER_MODE;
    private Boolean POST_PREMODERATION;
    private Boolean STATISTICS_IS_PUBLIC;

    public ResponseSettings(Map<String, Boolean> settings) {
        for (String key : settings.keySet()) {
            switch (key) {
                case ("MULTIUSER_MODE"):
                    MULTIUSER_MODE = settings.get("MULTIUSER_MODE");
                    break;
                case ("POST_PREMODERATION"):
                    POST_PREMODERATION = settings.get("POST_PREMODERATION");
                    break;
                case ("STATISTICS_IS_PUBLIC"):
                    STATISTICS_IS_PUBLIC = settings.get("STATISTICS_IS_PUBLIC");
                    break;
            }
        }
    }

    public ResponseSettings(Set<GlobalSettings> settings) {
        for (GlobalSettings g : settings) {
            String settingName = g.getName().toUpperCase();
            switch (settingName) {
                case "MULTIUSER_MODE": {
                    MULTIUSER_MODE = yesOrNoToBoolean(g.getValue());
                    break;
                }
                case "POST_PREMODERATION": {
                    POST_PREMODERATION = yesOrNoToBoolean(g.getValue());
                    break;
                }
                case "STATISTICS_IS_PUBLIC": {
                    STATISTICS_IS_PUBLIC = yesOrNoToBoolean(g.getValue());
                    break;
                }
            }
        }
    }

    public Boolean getMULTIUSER_MODE() {
        return MULTIUSER_MODE;
    }

    public void setMULTIUSER_MODE(Boolean MULTIUSER_MODE) {
        this.MULTIUSER_MODE = MULTIUSER_MODE;
    }

    public Boolean getPOST_PREMODERATION() {
        return POST_PREMODERATION;
    }

    public void setPOST_PREMODERATION(Boolean POST_PREMODERATION) {
        this.POST_PREMODERATION = POST_PREMODERATION;
    }

    public Boolean getSTATISTICS_IS_PUBLIC() {
        return STATISTICS_IS_PUBLIC;
    }

    public void setSTATISTICS_IS_PUBLIC(Boolean STATISTICS_IS_PUBLIC) {
        this.STATISTICS_IS_PUBLIC = STATISTICS_IS_PUBLIC;
    }

    private String booleanToYesOrNo(boolean bool) {
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

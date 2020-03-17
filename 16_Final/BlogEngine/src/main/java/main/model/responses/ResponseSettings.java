package main.model.responses;

import main.model.entities.GlobalSettings;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ResponseSettings implements ResponseAPI {

    private Map<String, Boolean> map;

    public ResponseSettings(Set<GlobalSettings> settings) {
        map = new HashMap<>();
        for (GlobalSettings g : settings) {
            boolean currentSetting = false;
            if (g.getValue().toUpperCase().equals("YES")) {
                currentSetting = true;
            }
            map.put(g.getName(), currentSetting);
        }
    }

    public Map<String, Boolean> getMap() {
        return map;
    }

    public void setMap(Map<String, Boolean> map) {
        this.map = map;
    }
}

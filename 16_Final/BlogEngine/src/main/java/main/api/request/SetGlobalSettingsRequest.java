package main.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import main.services.interfaces.GlobalSettingsRepositoryService;

public class SetGlobalSettingsRequest implements RequestApi {

    @JsonProperty(GlobalSettingsRepositoryService.MULTIUSER_MODE)
    private Boolean MULTIUSER_MODE = null;
    @JsonProperty(GlobalSettingsRepositoryService.POST_PREMODERATION)
    private Boolean POST_PREMODERATION = null;
    @JsonProperty(GlobalSettingsRepositoryService.STATISTICS_IS_PUBLIC)
    private Boolean STATISTICS_IS_PUBLIC = null;

    public SetGlobalSettingsRequest() {
    }

    public SetGlobalSettingsRequest(Boolean MULTIUSER_MODE, Boolean POST_PREMODERATION, Boolean STATISTICS_IS_PUBLIC) {
        this.MULTIUSER_MODE = MULTIUSER_MODE;
        this.POST_PREMODERATION = POST_PREMODERATION;
        this.STATISTICS_IS_PUBLIC = STATISTICS_IS_PUBLIC;
    }

    public Boolean getMultiuserMode() {
        return MULTIUSER_MODE;
    }

    public void setMultiuserMode(Boolean MULTIUSER_MODE) {
        this.MULTIUSER_MODE = MULTIUSER_MODE;
    }

    public Boolean getPostPremoderation() {
        return POST_PREMODERATION;
    }

    public void setPostPremoderation(Boolean POST_PREMODERATION) {
        this.POST_PREMODERATION = POST_PREMODERATION;
    }

    public Boolean getStatisticsIsPublic() {
        return STATISTICS_IS_PUBLIC;
    }

    public void setStatisticsIsPublic(Boolean STATISTICS_IS_PUBLIC) {
        this.STATISTICS_IS_PUBLIC = STATISTICS_IS_PUBLIC;
    }
}

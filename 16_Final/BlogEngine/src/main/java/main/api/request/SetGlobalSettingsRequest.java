package main.api.request;

public class SetGlobalSettingsRequest implements RequestApi {

    private Boolean MULTIUSER_MODE = null;
    private Boolean POST_PREMODERATION = null;
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

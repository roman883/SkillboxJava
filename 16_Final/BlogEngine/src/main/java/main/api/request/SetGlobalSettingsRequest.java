package main.api.request;

public class SetGlobalSettingsRequest implements RequestApi {

    private Boolean MULTIUSER_MODE;
    private Boolean POST_PREMODERATION;
    private Boolean STATISTICS_IS_PUBLIC;

    public SetGlobalSettingsRequest() {
        MULTIUSER_MODE = false;
        POST_PREMODERATION = false;
        STATISTICS_IS_PUBLIC = false;
    }

    public SetGlobalSettingsRequest(Boolean MULTIUSER_MODE, Boolean POST_PREMODERATION, Boolean STATISTICS_IS_PUBLIC) {
        this.MULTIUSER_MODE = MULTIUSER_MODE;
        this.POST_PREMODERATION = POST_PREMODERATION;
        this.STATISTICS_IS_PUBLIC = STATISTICS_IS_PUBLIC;
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
}

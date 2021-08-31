package cm.aptoide.pt.dataprovider.model.smart;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AddedApp {
    @JsonProperty("json")
    String json;

    @JsonProperty("appType")
    String appType;

    @JsonProperty("appCategory")
    String appCategory;

    @JsonProperty("includeInTop")
    boolean includeInTop;

    @JsonProperty("includeInLatest")
    boolean includeInLatest;

    public String getJson() {
        return json;
    }

    public String getAppType() {
        return appType;
    }

    public String getAppCategory() {
        return appCategory;
    }

    public boolean isIncludeInTop() {
        return includeInTop;
    }

    public boolean isIncludeInLatest() {
        return includeInLatest;
    }

    @Override
    public String toString() {
        return "AddApp: json = " + json + ", appType = " + appType + ", appCategory = " + appCategory
                + ", includeInLatest = " + includeInLatest + ", includeInTop = " + includeInLatest;
    }
}
package cm.aptoide.pt.dataprovider.model.smart;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

// Adding this will allow us to add properties to the .json file in the future & not have the app
// throw an exception if it's run against an older version of the code
@JsonIgnoreProperties(ignoreUnknown = true)
public class Platform {

    @JsonProperty("removed")
    private List<RemovedApp> removed;

    @JsonProperty("filtered")
    private List<FilteredApp> filtered;

    @JsonProperty("added")
    private List<AddedApp> added;

    @JsonProperty("platform")
    private String platform;

    public List<RemovedApp> getRemoved() {
        return removed;
    }

    public List<FilteredApp> getFiltered() {
        return filtered;
    }

    public List<AddedApp> getAdded() {
        return added;
    }

    public String getPlatform() {
        return platform;
    }

    public void setRemoved(List<RemovedApp> removed) {
        this.removed = removed;
    }

    public void setFiltered(List<FilteredApp> filtered) {
        this.filtered = filtered;
    }

    public void setAdded(List<AddedApp> added) {
        this.added = added;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    @Override
	public String toString() {
        return "Platform {removed=" + removed + ", filtered=" + filtered + ", added=" + added +
				", platform='" + platform + "'}";
	}
}
package cm.aptoide.pt.dataprovider.model.smart;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Platform {

    @JsonProperty("removed")
    private List<RemovedApp> removed;

    @JsonProperty("filtered")
    private List<FilteredApp> filtered;

    @JsonProperty("platform")
    private String platform;

    public List<RemovedApp> getRemoved() {
        return removed;
    }

    public List<FilteredApp> getFiltered() {
        return filtered;
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

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    @Override
	public String toString() {
		return "Platform{" +
				"removed=" + removed +
				", filtered=" + filtered +
				", platform='" + platform + '\'' +
				'}';
	}
}
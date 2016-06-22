package cm.aptoide.pt.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Data;

@Data
public class StoreLatestAppsTimelineItem implements TimelineItem<StoreLatestApps> {

	private List<StoreLatestApps> latestAppsList;

	@JsonCreator public StoreLatestAppsTimelineItem(@JsonProperty("items") List<StoreLatestApps> latestAppsList) {
		this.latestAppsList = latestAppsList;
	}

	@Override
	public List<StoreLatestApps> getItems() {
		return latestAppsList;
	}
}

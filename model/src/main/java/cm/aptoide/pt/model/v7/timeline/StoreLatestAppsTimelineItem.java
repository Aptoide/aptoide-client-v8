package cm.aptoide.pt.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class StoreLatestAppsTimelineItem implements TimelineItem<TimelineCard> {

	private StoreLatestApps latestApps;

	@JsonCreator public StoreLatestAppsTimelineItem(@JsonProperty("data") StoreLatestApps latestApps) {
		this.latestApps = latestApps;
	}

	@Override
	public StoreLatestApps getData() {
		return latestApps;
	}
}

package cm.aptoide.pt.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import cm.aptoide.pt.model.v7.timeline.LatestApps;
import cm.aptoide.pt.model.v7.timeline.TimelineItem;
import lombok.Data;

@Data
public class LatestAppsTimelineItem implements TimelineItem<LatestApps> {

	private List<LatestApps> latestAppsList;

	@JsonCreator public LatestAppsTimelineItem(@JsonProperty("items") List<LatestApps> latestAppsList) {
		this.latestAppsList = latestAppsList;
	}

	@Override
	public List<LatestApps> getItems() {
		return latestAppsList;
	}
}

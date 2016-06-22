package cm.aptoide.pt.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Data;

/**
 * Created by marcelobenites on 6/17/16.
 */
@Data
public class AppsUpdatesTimelineItem implements TimelineItem<AppsUpdates> {

	private final List<AppsUpdates> updates;

	@JsonCreator public AppsUpdatesTimelineItem(@JsonProperty("items") List<AppsUpdates> updates) {
		this.updates = updates;
	}

	@Override
	public List<AppsUpdates> getItems() {
		return updates;
	}
}

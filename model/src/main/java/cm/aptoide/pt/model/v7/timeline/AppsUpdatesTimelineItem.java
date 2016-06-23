package cm.aptoide.pt.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Data;

/**
 * Created by marcelobenites on 6/17/16.
 */
@Data
public class AppsUpdatesTimelineItem implements TimelineItem<AppUpdate> {

	private final AppUpdate updates;

	@JsonCreator public AppsUpdatesTimelineItem(@JsonProperty("data") AppUpdate updates) {
		this.updates = updates;
	}

	@Override
	public AppUpdate getData() {
		return updates;
	}
}

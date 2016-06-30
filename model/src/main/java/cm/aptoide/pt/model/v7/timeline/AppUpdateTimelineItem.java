package cm.aptoide.pt.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import cm.aptoide.pt.model.v7.listapp.App;
import lombok.Data;

/**
 * Created by marcelobenites on 6/17/16.
 */
@Data
public class AppUpdateTimelineItem implements TimelineItem<App> {

	private final App app;

	@JsonCreator public AppUpdateTimelineItem(@JsonProperty("data") App app) {
		this.app = app;
	}

	@Override
	public App getData() {
		return app;
	}
}

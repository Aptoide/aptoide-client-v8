package cm.aptoide.pt.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = ArticleTimelineItem.class, name = "ARTICLES"),
		@JsonSubTypes.Type(value = FeatureTimelineItem.class, name = "FEATURES"),
		@JsonSubTypes.Type(value = LatestAppsTimelineItem.class, name = "APPS_LATEST"),
		@JsonSubTypes.Type(value = AppsUpdatesTimelineItem.class, name = "APPS_UPDATES")
})
public interface TimelineItem<T> {

	List<T> getItems();
}

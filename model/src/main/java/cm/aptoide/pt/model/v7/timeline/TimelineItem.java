package cm.aptoide.pt.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", defaultImpl = Void.class)
@JsonSubTypes({
		@JsonSubTypes.Type(value = ArticleTimelineItem.class, name = "ARTICLE"),
		@JsonSubTypes.Type(value = FeatureTimelineItem.class, name = "FEATURE"),
		@JsonSubTypes.Type(value = StoreLatestAppsTimelineItem.class, name = "APPS_LATEST"),
		@JsonSubTypes.Type(value = AppUpdateTimelineItem.class, name = "APP_UPDATE"),
		@JsonSubTypes.Type(value = RecommendationTimelineItem.class, name = "RECOMMENDATION"),
		@JsonSubTypes.Type(value = VideoTimelineItem.class, name = "VIDEO"),
})
public interface TimelineItem<T> {

	T getData();

}
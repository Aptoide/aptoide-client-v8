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
    @JsonSubTypes.Type(value = RecommendationTimelineItem.class, name = "SIMILAR"),
    @JsonSubTypes.Type(value = SocialArticleTimelineItem.class, name = "SOCIAL_ARTICLE"),
    @JsonSubTypes.Type(value = SocialVideoTimelineItem.class, name = "SOCIAL_VIDEO"),
    @JsonSubTypes.Type(value = SocialStoreLatestAppsTimelineItem.class, name = "SOCIAL_STORE"),
    @JsonSubTypes.Type(value = SocialInstallTimelineItem.class, name = "SOCIAL_INSTALL"),
    @JsonSubTypes.Type(value = SocialRecommendationTimelineItem.class, name = "SOCIAL_APP"),
    @JsonSubTypes.Type(value = PopularAppTimelineItem.class, name = "POPULAR_APP"),
    @JsonSubTypes.Type(value = AggregatedSocialInstallTimelineItem.class, name = "AGGREGATED_SOCIAL_INSTALL")
}) public interface TimelineItem<T> {

  T getData();
}
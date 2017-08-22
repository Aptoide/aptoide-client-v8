package cm.aptoide.pt.dataprovider.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

/**
 * Created by jdandrade on 17/05/2017.
 */

@EqualsAndHashCode public class AggregatedSocialArticleTimelineItem
    implements TimelineItem<TimelineCard> {
  private final AggregatedSocialArticle aggregatedSocialArticle;

  @JsonCreator public AggregatedSocialArticleTimelineItem(
      @JsonProperty("data") AggregatedSocialArticle aggregatedSocialArticle) {
    this.aggregatedSocialArticle = aggregatedSocialArticle;
  }

  @Override public Ab getAb() {
    return this.aggregatedSocialArticle.getAb();
  }

  @Override public AggregatedSocialArticle getData() {
    return this.aggregatedSocialArticle;
  }
}

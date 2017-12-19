package cm.aptoide.pt.dataprovider.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jdandrade on 17/05/2017.
 */

public class AggregatedSocialArticleTimelineItem implements TimelineItem<TimelineCard> {
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

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $aggregatedSocialArticle = this.aggregatedSocialArticle;
    result = result * PRIME + ($aggregatedSocialArticle == null ? 43
        : $aggregatedSocialArticle.hashCode());
    return result;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof AggregatedSocialArticleTimelineItem)) return false;
    final AggregatedSocialArticleTimelineItem other = (AggregatedSocialArticleTimelineItem) o;
    if (!other.canEqual((Object) this)) return false;
    final Object this$aggregatedSocialArticle = this.aggregatedSocialArticle;
    final Object other$aggregatedSocialArticle = other.aggregatedSocialArticle;
    if (this$aggregatedSocialArticle == null ? other$aggregatedSocialArticle != null
        : !this$aggregatedSocialArticle.equals(other$aggregatedSocialArticle)) {
      return false;
    }
    return true;
  }

  protected boolean canEqual(Object other) {
    return other instanceof AggregatedSocialArticleTimelineItem;
  }
}

package cm.aptoide.pt.dataprovider.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jdandrade on 23/11/2016.
 */

public class SocialArticleTimelineItem implements TimelineItem<TimelineCard> {
  private final SocialArticle socialArticle;

  @JsonCreator public SocialArticleTimelineItem(@JsonProperty("data") SocialArticle socialArticle) {
    this.socialArticle = socialArticle;
  }

  @Override public Ab getAb() {
    return this.socialArticle.getAb();
  }

  @Override public SocialArticle getData() {
    return socialArticle;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $socialArticle = this.socialArticle;
    result = result * PRIME + ($socialArticle == null ? 43 : $socialArticle.hashCode());
    return result;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof SocialArticleTimelineItem)) return false;
    final SocialArticleTimelineItem other = (SocialArticleTimelineItem) o;
    if (!other.canEqual(this)) return false;
    final Object this$socialArticle = this.socialArticle;
    final Object other$socialArticle = other.socialArticle;
    return this$socialArticle == null ? other$socialArticle == null
        : this$socialArticle.equals(other$socialArticle);
  }

  protected boolean canEqual(Object other) {
    return other instanceof SocialArticleTimelineItem;
  }
}

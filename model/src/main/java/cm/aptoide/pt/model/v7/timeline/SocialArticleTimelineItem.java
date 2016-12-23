package cm.aptoide.pt.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

/**
 * Created by jdandrade on 23/11/2016.
 */

@EqualsAndHashCode public class SocialArticleTimelineItem implements TimelineItem<TimelineCard> {
  private final SocialArticle socialArticle;

  @JsonCreator public SocialArticleTimelineItem(@JsonProperty("data") SocialArticle socialArticle) {
    this.socialArticle = socialArticle;
  }

  @Override public TimelineCard getData() {
    return socialArticle;
  }
}

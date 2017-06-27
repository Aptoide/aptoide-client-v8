/*
 * Copyright (c) 2016.
 * Modified on 06/07/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode public class ArticleTimelineItem implements TimelineItem<TimelineCard> {

  private final Article article;

  @JsonCreator public ArticleTimelineItem(@JsonProperty("data") Article article) {
    this.article = article;
  }

  @Override public Article getData() {
    return article;
  }
}

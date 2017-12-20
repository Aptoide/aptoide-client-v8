/*
 * Copyright (c) 2016.
 * Modified on 06/07/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ArticleTimelineItem implements TimelineItem<TimelineCard> {

  private final Article article;

  @JsonCreator public ArticleTimelineItem(@JsonProperty("data") Article article) {
    this.article = article;
  }

  @Override public Ab getAb() {
    return this.article.getAb();
  }

  @Override public Article getData() {
    return article;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $article = this.article;
    result = result * PRIME + ($article == null ? 43 : $article.hashCode());
    return result;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof ArticleTimelineItem)) return false;
    final ArticleTimelineItem other = (ArticleTimelineItem) o;
    if (!other.canEqual(this)) return false;
    final Object this$article = this.article;
    final Object other$article = other.article;
    return this$article == null ? other$article == null : this$article.equals(other$article);
  }

  protected boolean canEqual(Object other) {
    return other instanceof ArticleTimelineItem;
  }
}

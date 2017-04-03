package cm.aptoide.pt.v8engine.comments;

import cm.aptoide.pt.model.v7.Review;
import lombok.Data;

@Data public final class ReviewWithAppName {
  private final String appName;
  private final Review review;

  public ReviewWithAppName(String appName, Review review) {
    this.appName = appName;
    this.review = review;
  }
}

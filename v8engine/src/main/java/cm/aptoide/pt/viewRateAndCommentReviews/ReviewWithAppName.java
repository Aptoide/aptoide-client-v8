package cm.aptoide.pt.viewRateAndCommentReviews;

import cm.aptoide.pt.model.v7.Review;
import lombok.Data;

@Data final class ReviewWithAppName {
  private final String appName;
  private final Review review;

  public ReviewWithAppName(String appName, Review review) {
    this.appName = appName;
    this.review = review;
  }
}

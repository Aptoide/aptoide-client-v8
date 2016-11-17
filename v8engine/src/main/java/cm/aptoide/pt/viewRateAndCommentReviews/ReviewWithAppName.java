package cm.aptoide.pt.viewRateAndCommentReviews;

import cm.aptoide.pt.model.v7.Review;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor @Data final class ReviewWithAppName {
  private final String appName;
  private final Review review;
}

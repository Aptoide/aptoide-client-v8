package cm.aptoide.pt.comments;

import cm.aptoide.pt.dataprovider.model.v7.Review;

public final class ReviewWithAppName {
  private final String appName;
  private final Review review;

  public String getAppName() {
    return appName;
  }

  public Review getReview() {
    return review;
  }

  public ReviewWithAppName(String appName, Review review) {
    this.appName = appName;
    this.review = review;
  }
}

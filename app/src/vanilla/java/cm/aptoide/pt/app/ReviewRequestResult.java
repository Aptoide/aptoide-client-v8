package cm.aptoide.pt.app;

import cm.aptoide.pt.dataprovider.model.v7.Review;
import java.util.Collections;
import java.util.List;

/**
 * Created by D01 on 10/05/2018.
 */

public class ReviewRequestResult {
  private final List<Review> reviewList;
  private final boolean loading;
  private final Error error;

  public ReviewRequestResult(boolean loading) {
    this.reviewList = Collections.emptyList();
    this.loading = loading;
    this.error = null;
  }

  public ReviewRequestResult(Error error) {
    this.reviewList = Collections.emptyList();
    this.loading = false;
    this.error = error;
  }

  public ReviewRequestResult(List<Review> reviewList) {
    this.reviewList = reviewList;
    this.loading = false;
    this.error = null;
  }

  public boolean hasError() {
    return (error != null);
  }

  public Error getError() {
    return error;
  }

  public List<Review> getReviewList() {
    return reviewList;
  }

  public boolean isLoading() {
    return loading;
  }

  public enum Error {
    NETWORK, GENERIC
  }
}

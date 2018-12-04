package cm.aptoide.pt.app;

import java.util.Collections;
import java.util.List;

/**
 * Created by D01 on 10/05/2018.
 */

public class ReviewRequestResult {
  private final List<AppReview> reviewList;
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

  public ReviewRequestResult(List<AppReview> reviewList) {
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

  public List<AppReview> getReviewList() {
    return reviewList;
  }

  public boolean isLoading() {
    return loading;
  }

  public enum Error {
    NETWORK, GENERIC
  }
}

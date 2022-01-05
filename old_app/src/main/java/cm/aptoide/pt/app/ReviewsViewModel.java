package cm.aptoide.pt.app;

import java.util.List;

/**
 * Created by D01 on 07/05/18.
 */

public class ReviewsViewModel {

  private final List<AppReview> reviewsList;
  private final boolean loading;
  private final ReviewRequestResult.Error error;

  public ReviewsViewModel(List<AppReview> reviewsList, boolean loading,
      ReviewRequestResult.Error error) {

    this.reviewsList = reviewsList;
    this.loading = loading;
    this.error = error;
  }

  public List<AppReview> getReviewsList() {
    return reviewsList;
  }

  public boolean isLoading() {
    return loading;
  }

  public ReviewRequestResult.Error getError() {
    return error;
  }

  public boolean hasError() {
    return (error != null);
  }
}

package cm.aptoide.pt.app;

import cm.aptoide.pt.dataprovider.model.v7.Review;
import java.util.List;

/**
 * Created by D01 on 07/05/18.
 */

public class ReviewsViewModel {

  private final List<Review> reviewsList;
  private final boolean loading;
  private final ReviewRequestResult.Error error;

  public ReviewsViewModel(List<Review> reviewsList, boolean loading,
      ReviewRequestResult.Error error) {

    this.reviewsList = reviewsList;
    this.loading = loading;
    this.error = error;
  }

  public List<Review> getReviewsList() {
    return reviewsList;
  }

  public boolean isLoading() {
    return loading;
  }

  public ReviewRequestResult.Error getError() {
    return error;
  }
}

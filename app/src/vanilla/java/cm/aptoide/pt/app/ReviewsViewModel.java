package cm.aptoide.pt.app;

import cm.aptoide.pt.dataprovider.model.v7.Review;
import java.util.List;

/**
 * Created by D01 on 07/05/18.
 */

public class ReviewsViewModel {

  private final List<Review> reviewsList;

  public ReviewsViewModel(List<Review> reviewsList) {

    this.reviewsList = reviewsList;
  }

  public List<Review> getReviewsList() {
    return reviewsList;
  }
}

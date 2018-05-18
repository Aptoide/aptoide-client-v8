package cm.aptoide.pt.app;

import rx.Single;

/**
 * Created by D01 on 04/05/18.
 */

public class ReviewsRepository {

  private final ReviewsService reviewsService;

  public ReviewsRepository(ReviewsService reviewsService) {

    this.reviewsService = reviewsService;
  }

  public Single<ReviewRequestResult> loadListReviews(String storeName, String packageName,
      int maxReviews, String languagesFilterSort) {
    return reviewsService.loadListReviews(storeName, packageName, maxReviews, languagesFilterSort);
  }
}

package cm.aptoide.pt.app;

import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
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

  public Single<BaseV7Response> doReviewRatingRequest(long reviewId, boolean helpful) {
    return reviewsService.doReviewRatingRequest(reviewId, helpful);
  }
}

package cm.aptoide.pt.app;

import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import rx.Single;

/**
 * Created by D01 on 04/05/18.
 */

public class ReviewsManager {

  private final ReviewsRepository reviewsRepository;

  public ReviewsManager(ReviewsRepository reviewsRepository) {

    this.reviewsRepository = reviewsRepository;
  }

  public Single<ReviewRequestResult> loadReviews(String storeName, String packageName,
      int maxReviews, String languagesFilterSort) {
    return reviewsRepository.loadListReviews(storeName, packageName, maxReviews,
        languagesFilterSort);
  }

  public Single<BaseV7Response> doReviewRatingRequest(long reviewId, boolean helpful) {
    return reviewsRepository.doReviewRatingRequest(reviewId, helpful);
  }
}

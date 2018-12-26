package cm.aptoide.pt.app;

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
    return reviewsRepository.loadReviews(storeName, packageName, maxReviews, languagesFilterSort);
  }
}

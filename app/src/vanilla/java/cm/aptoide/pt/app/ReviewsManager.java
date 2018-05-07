package cm.aptoide.pt.app;

import cm.aptoide.pt.dataprovider.model.v7.Review;
import cm.aptoide.pt.view.app.DetailedApp;
import java.util.List;
import rx.Single;

/**
 * Created by D01 on 04/05/18.
 */

public class ReviewsManager {

  private final ReviewsRepository reviewsRepository;

  public ReviewsManager(ReviewsRepository reviewsRepository) {

    this.reviewsRepository = reviewsRepository;
  }

  public Single<List<Review>> loadReviews(String storeName, String packageName, int maxReviews,
      String languagesFilterSort, DetailedApp detailedApp) {
    return reviewsRepository.loadListReviews(storeName, packageName, maxReviews,
        languagesFilterSort, detailedApp);
  }
}

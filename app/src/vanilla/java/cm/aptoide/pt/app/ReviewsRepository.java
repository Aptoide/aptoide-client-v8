package cm.aptoide.pt.app;

import cm.aptoide.pt.dataprovider.model.v7.Review;
import cm.aptoide.pt.view.app.DetailedApp;
import java.util.List;
import rx.Single;

/**
 * Created by D01 on 04/05/18.
 */

public class ReviewsRepository {

  private final ReviewsService reviewsService;

  public ReviewsRepository(ReviewsService reviewsService) {

    this.reviewsService = reviewsService;
  }

  public Single<List<Review>> loadListReviews(String storeName, String packageName, int maxReviews,
      String languagesFilterSort, DetailedApp detailedApp) {
    return reviewsService.loadListReviews(storeName, packageName, maxReviews, languagesFilterSort,
        detailedApp);
  }
}

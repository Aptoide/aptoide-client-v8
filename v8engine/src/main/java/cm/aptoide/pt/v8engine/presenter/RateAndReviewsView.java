package cm.aptoide.pt.v8engine.presenter;

import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.model.v7.Review;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.v8engine.presenter.View;
import java.util.List;
import rx.Observable;

public interface RateAndReviewsView extends View {
  /**
   * @return Observable that emits the next offset.
   */
  Observable<Integer> nextReviews();

  Observable<Void> rateApp();

  Observable<GenericDialogs.EResponse> showRateView();

  void showNextReviews(int offset, List<Review> reviews);

  void showRating(GetAppMeta.Stats.Rating rating);

  void showError(Throwable err);
}

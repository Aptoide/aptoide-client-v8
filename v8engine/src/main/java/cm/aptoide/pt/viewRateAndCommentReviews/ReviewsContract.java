package cm.aptoide.pt.viewRateAndCommentReviews;

import cm.aptoide.pt.BasePresenter;
import cm.aptoide.pt.BaseView;
import cm.aptoide.pt.model.v7.Review;
import java.util.List;

public interface ReviewsContract {

  interface View extends BaseView<Presenter> {
    void setLoadingIndicator(boolean visible);
    boolean isActive();
    void showNoReviews();
    void showReviews(List<Review> reviews);
  }

  interface Presenter extends BasePresenter {
    
  }
}

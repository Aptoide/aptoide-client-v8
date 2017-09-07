package cm.aptoide.pt.view.reviews;

import android.support.annotation.NonNull;
import android.view.View;
import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.view.recycler.widget.Widget;

/**
 * Created by neuro on 04-09-2017.
 */
public class ReviewsRatingWidget extends Widget<ReviewsRatingDisplayable> {

  private RatingTotalsLayout ratingTotalsLayout;
  private RatingBarsLayout ratingBarsLayout;

  public ReviewsRatingWidget(@NonNull View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    ratingTotalsLayout = new RatingTotalsLayout(itemView);
    ratingBarsLayout = new RatingBarsLayout(itemView);
  }

  @Override public void bindView(ReviewsRatingDisplayable displayable) {
    setupRating(displayable.getAppMeta());
  }

  private void setupRating(GetAppMeta.App data) {
    ratingTotalsLayout.setup(data);
    ratingBarsLayout.setup(data);
  }
}

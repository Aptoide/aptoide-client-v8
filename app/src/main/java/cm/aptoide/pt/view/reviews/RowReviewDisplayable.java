package cm.aptoide.pt.view.reviews;

import cm.aptoide.pt.dataprovider.model.v7.FullReview;
import cm.aptoide.pt.R;
import cm.aptoide.pt.view.recycler.displayable.DisplayablePojo;

public class RowReviewDisplayable extends DisplayablePojo<FullReview> {

  public RowReviewDisplayable() {
  }

  public RowReviewDisplayable(FullReview pojo) {
    super(pojo);
  }

  @Override protected Configs getConfig() {
    return new Configs(1, false);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_row_review;
  }
}

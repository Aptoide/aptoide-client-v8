package cm.aptoide.pt.v8engine.view.reviews;

import cm.aptoide.pt.model.v7.FullReview;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;

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

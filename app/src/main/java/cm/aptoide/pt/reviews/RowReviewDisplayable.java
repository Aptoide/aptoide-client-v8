package cm.aptoide.pt.reviews;

import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.model.v7.FullReview;
import cm.aptoide.pt.store.StoreAnalytics;
import cm.aptoide.pt.view.recycler.displayable.DisplayablePojo;

public class RowReviewDisplayable extends DisplayablePojo<FullReview> {

  private StoreAnalytics storeAnalytics;

  public RowReviewDisplayable() {
  }

  public RowReviewDisplayable(FullReview pojo) {
    super(pojo);
  }

  public RowReviewDisplayable(FullReview pojo, StoreAnalytics storeAnalytics) {
    super(pojo);
    this.storeAnalytics = storeAnalytics;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, false);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_row_review;
  }

  public StoreAnalytics getStoreAnalytics() {
    return storeAnalytics;
  }
}

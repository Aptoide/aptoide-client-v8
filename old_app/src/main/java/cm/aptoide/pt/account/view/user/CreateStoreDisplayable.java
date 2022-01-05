package cm.aptoide.pt.account.view.user;

import androidx.annotation.ColorInt;
import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.model.v7.TimelineStats;
import cm.aptoide.pt.store.StoreAnalytics;
import cm.aptoide.pt.view.recycler.displayable.Displayable;

/**
 * Created by trinkes on 02/12/2016.
 */

public class CreateStoreDisplayable extends Displayable {

  private StoreAnalytics storeAnalytics;
  private TimelineStats timelineStats;
  private int textAccentColor;

  public CreateStoreDisplayable() {
  }

  public CreateStoreDisplayable(StoreAnalytics storeAnalytics, TimelineStats timelineStats,
      @ColorInt int textAccentColor) {
    this.storeAnalytics = storeAnalytics;
    this.timelineStats = timelineStats;
    this.textAccentColor = textAccentColor;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.create_store_displayable_layout;
  }

  public StoreAnalytics getStoreAnalytics() {
    return storeAnalytics;
  }

  public long getFollowers() {
    return timelineStats.getData()
        .getFollowers();
  }

  public long getFollowings() {
    return timelineStats.getData()
        .getFollowing();
  }

  public int getTextAccentColor() {
    return textAccentColor;
  }
}

package cm.aptoide.pt.view.account.user;

import cm.aptoide.pt.R;
import cm.aptoide.pt.store.StoreAnalytics;
import cm.aptoide.pt.view.recycler.displayable.Displayable;

/**
 * Created by trinkes on 02/12/2016.
 */

public class CreateStoreDisplayable extends Displayable {

  private StoreAnalytics storeAnalytics;

  public CreateStoreDisplayable() {
  }

  public CreateStoreDisplayable(StoreAnalytics storeAnalytics) {
    this.storeAnalytics = storeAnalytics;
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
}

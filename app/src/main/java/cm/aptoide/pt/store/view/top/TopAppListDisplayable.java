package cm.aptoide.pt.store.view.top;

import cm.aptoide.analytics.implementation.navigation.NavigationTracker;
import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.view.recycler.displayable.DisplayablePojo;

public class TopAppListDisplayable extends DisplayablePojo<App> {

  private String tag;
  private NavigationTracker navigationTracker;
  private StoreContext storeContext;

  public TopAppListDisplayable() {
  }

  public TopAppListDisplayable(App pojo, String tag, NavigationTracker navigationTracker,
      StoreContext storeContext) {
    super(pojo);
    this.tag = tag;
    this.navigationTracker = navigationTracker;
    this.storeContext = storeContext;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, false);
  }

  @Override public int getViewLayout() {
    return R.layout.top_app_item;
  }

  public NavigationTracker getNavigationTracker() {
    return navigationTracker;
  }

  public StoreContext getStoreContext() {
    return storeContext;
  }

  public String getTag() {
    return this.tag;
  }
}

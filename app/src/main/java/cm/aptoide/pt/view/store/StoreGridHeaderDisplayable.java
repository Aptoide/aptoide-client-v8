package cm.aptoide.pt.view.store;

import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.model.v7.GetStoreWidgets;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.view.recycler.displayable.Displayable;
import lombok.Getter;
import lombok.Setter;

public class StoreGridHeaderDisplayable extends Displayable {

  @Getter private final GetStoreWidgets.WSWidget wsWidget;
  @Getter private final String storeTheme;
  @Getter private final String tag;
  @Getter private StoreContext storeContext;
  @Getter @Setter private boolean moreVisible;
  @Getter private final StoreTabNavigator storeTabNavigator;

  // this constructor is necessary due to reflection code that generates displayables. that code
  // needs to go as this.
  public StoreGridHeaderDisplayable() {
    this(null, null, null, null, null);
  }

  public StoreGridHeaderDisplayable(GetStoreWidgets.WSWidget wsWidget, String storeTheme,
      String tag, StoreContext storeContext, StoreTabNavigator storeTabNavigator) {
    this.wsWidget = wsWidget;
    this.storeTheme = storeTheme;
    this.tag = tag;
    this.storeContext = storeContext;
    this.storeTabNavigator = storeTabNavigator;
    this.moreVisible = true;
  }

  public StoreGridHeaderDisplayable(GetStoreWidgets.WSWidget wsWidget,
      StoreTabNavigator storeTabNavigator) {
    this(wsWidget, null, null, null, storeTabNavigator);
  }

  @Override protected Displayable.Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_grid_header;
  }
}

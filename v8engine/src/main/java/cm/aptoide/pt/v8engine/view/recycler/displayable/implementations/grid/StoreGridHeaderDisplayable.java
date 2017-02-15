package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.pt.model.v7.GetStoreWidgets;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import lombok.Getter;
import lombok.Setter;

public class StoreGridHeaderDisplayable extends Displayable {

  @Getter private final GetStoreWidgets.WSWidget wsWidget;
  @Getter private final String storeTheme;
  @Getter private final String tag;
  @Getter @Setter private boolean moreVisible;

  public StoreGridHeaderDisplayable() {
    this(null, null, null);
  }

  public StoreGridHeaderDisplayable(GetStoreWidgets.WSWidget wsWidget, String storeTheme,
      String tag) {
    this.wsWidget = wsWidget;
    this.storeTheme = storeTheme;
    this.tag = tag;
    this.moreVisible = true;
  }

  public StoreGridHeaderDisplayable(GetStoreWidgets.WSWidget wsWidget) {
    this(wsWidget, null, null);
  }

  @Override protected Displayable.Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_grid_header;
  }
}

package cm.aptoide.pt.app.view;

import android.util.Pair;
import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.store.StoreTheme;
import cm.aptoide.pt.view.recycler.displayable.Displayable;

public class OfficialAppDisplayable extends Displayable {

  private final Pair<String, GetAppMeta> messageGetApp;
  private final StoreTheme storeTheme;


  public OfficialAppDisplayable() {
    messageGetApp = null;
    storeTheme = StoreTheme.DEFAULT;
  }

  public OfficialAppDisplayable(Pair<String, GetAppMeta> messageGetApp, StoreTheme storeTheme) {
    this.messageGetApp = messageGetApp;
    this.storeTheme = storeTheme;
  }

  public Pair<String, GetAppMeta> getMessageGetApp() {
    return messageGetApp;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.official_app_displayable_layout;
  }

  public StoreTheme getStoreTheme() {
    return storeTheme;
  }
}

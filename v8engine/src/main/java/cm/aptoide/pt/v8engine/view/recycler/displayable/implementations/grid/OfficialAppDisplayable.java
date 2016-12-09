package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;

public class OfficialAppDisplayable extends DisplayablePojo<GetApp> {

  public OfficialAppDisplayable() { }

  public OfficialAppDisplayable(GetApp app) {
    super(app);
  }

  @Override public int getViewLayout() {
    return R.layout.official_app_displayable_layout;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }
}

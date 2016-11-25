package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;

public class LatestStoreCommentsDisplayable extends Displayable {

  @Override public int getViewLayout() {
    return R.layout.displayable_grid_latest_store_comments;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }
}

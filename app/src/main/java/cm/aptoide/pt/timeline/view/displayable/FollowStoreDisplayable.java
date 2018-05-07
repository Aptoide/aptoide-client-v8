package cm.aptoide.pt.timeline.view.displayable;

import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.model.v7.Type;
import cm.aptoide.pt.view.recycler.displayable.Displayable;

public class FollowStoreDisplayable extends Displayable {

  @Override protected Configs getConfig() {
    return new Configs(Type.FOLLOW_STORE.getDefaultPerLineCount(),
        Type.FOLLOW_STORE.isFixedPerLineCount());
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_grid_follow_store;
  }
}

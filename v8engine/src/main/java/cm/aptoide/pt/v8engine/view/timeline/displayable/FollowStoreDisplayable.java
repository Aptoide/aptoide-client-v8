package cm.aptoide.pt.v8engine.view.timeline.displayable;

import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;

/**
 * Created by pedroribeiro on 22/02/17.
 */

public class FollowStoreDisplayable extends Displayable {

  @Override protected Configs getConfig() {
    return new Configs(Type.FOLLOW_STORE.getDefaultPerLineCount(),
        Type.FOLLOW_STORE.isFixedPerLineCount()); //todo: maybe add this displayable to type class and get default values from there
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_grid_follow_store;
  }
}

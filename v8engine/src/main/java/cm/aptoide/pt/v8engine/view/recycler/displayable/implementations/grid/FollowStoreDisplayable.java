package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;

/**
 * Created by pedroribeiro on 22/02/17.
 */

public class FollowStoreDisplayable extends Displayable {

  @Override protected Configs getConfig() {
    return new Configs(1,
        true); //todo: maybe add this displayable to type class and get default values from there
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_grid_follow_store;
  }
}

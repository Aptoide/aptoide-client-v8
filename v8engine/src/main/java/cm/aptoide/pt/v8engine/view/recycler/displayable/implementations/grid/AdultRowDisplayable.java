package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;

/**
 * Created by neuro on 11-07-2016.
 */
public class AdultRowDisplayable extends Displayable {

  @Override public int getViewLayout() {
    return R.layout.row_adult_switch;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }
}

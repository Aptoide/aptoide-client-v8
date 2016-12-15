package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.content.Context;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;

/**
 * Created by trinkes on 15/12/2016.
 */

public class TimeLineStatsDisplayable extends Displayable {

  @Override public int getViewLayout() {
    return R.layout.timeline_follows_info;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  public String getFollowersText(Context context) {
    return context.getString(R.string.followed);
  }

  public String getFollowingText(Context context) {
    return context.getString(R.string.followed);
  }
}

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.content.Context;
import cm.aptoide.pt.model.v7.TimelineStats;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;

/**
 * Created by trinkes on 15/12/2016.
 */

public class TimeLineStatsDisplayable extends DisplayablePojo<TimelineStats> {

  public TimeLineStatsDisplayable() {
  }

  public TimeLineStatsDisplayable(TimelineStats pojo) {
    super(pojo);
  }

  @Override public int getViewLayout() {
    return R.layout.timeline_follows_info;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  CharSequence getFollowersText(Context context) {
    return AptoideUtils.HtmlU.parse(
        String.format(String.valueOf(context.getText(R.string.timeline_followers)),
            getPojo().getData().getFollowers()));
  }

  CharSequence getFollowingText(Context context) {
    return AptoideUtils.HtmlU.parse(
        String.format(String.valueOf(context.getText(R.string.timeline_followed)),
            getPojo().getData().getFollowing()));
  }
}

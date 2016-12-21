package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.content.Context;
import android.graphics.Color;
import cm.aptoide.pt.model.v7.TimelineStats;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.fragment.implementations.TimeLineFollowFragment;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;

/**
 * Created by trinkes on 15/12/2016.
 */

public class TimeLineStatsDisplayable extends DisplayablePojo<TimelineStats> {

  private SpannableFactory spannableFactory;

  public TimeLineStatsDisplayable() {
  }

  public TimeLineStatsDisplayable(TimelineStats pojo, SpannableFactory spannableFactory) {
    super(pojo);
    this.spannableFactory = spannableFactory;
  }

  @Override public int getViewLayout() {
    return R.layout.timeline_follows_info;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  CharSequence getFollowersText(Context context) {
    return spannableFactory.createColorSpan(
        context.getString(R.string.timeline_followers, getPojo().getData().getFollowers()),
        Color.BLACK, String.valueOf(getPojo().getData().getFollowers()));
  }

  CharSequence getFollowingText(Context context) {
    return spannableFactory.createColorSpan(
        context.getString(R.string.timeline_followed, getPojo().getData().getFollowing()),
        Color.BLACK, String.valueOf(getPojo().getData().getFollowing()));
  }

  public Void followersClick(FragmentShower fragmentShower) {
    fragmentShower.pushFragmentV4(V8Engine.getFragmentProvider()
        .newTimeLineFollowStatsFragment(TimeLineFollowFragment.FollowFragmentOpenMode.FOLLOWERS,
            getPojo().getData().getFollowers()));
    return null;
  }

  public Void followingClick(FragmentShower fragmentShower) {
    fragmentShower.pushFragmentV4(V8Engine.getFragmentProvider()
        .newTimeLineFollowStatsFragment(TimeLineFollowFragment.FollowFragmentOpenMode.FOLLOWING,
            getPojo().getData().getFollowing()));
    return null;
  }
}

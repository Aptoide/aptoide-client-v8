package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.content.Context;
import android.graphics.Color;
import android.text.ParcelableSpan;
import android.text.style.ForegroundColorSpan;
import cm.aptoide.pt.model.v7.TimelineStats;
import cm.aptoide.pt.navigation.FragmentNavigator;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.repository.TimelineAnalytics;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import lombok.Getter;

/**
 * Created by trinkes on 15/12/2016.
 */

public class TimeLineStatsDisplayable extends DisplayablePojo<TimelineStats> {

  private Long userId;
  private SpannableFactory spannableFactory;
  private String storeTheme;
  @Getter private boolean shouldShowAddFriends;
  private TimelineAnalytics timelineAnalytics;
  private long storeId;

  public TimeLineStatsDisplayable() {
  }

  public TimeLineStatsDisplayable(TimelineStats pojo, Long userId,
      SpannableFactory spannableFactory, String storeTheme, TimelineAnalytics timelineAnalytics,
      boolean shouldShowAddFriends, long storeId) {
    super(pojo);
    this.userId = userId;
    this.spannableFactory = spannableFactory;
    this.storeTheme = storeTheme;
    this.shouldShowAddFriends = shouldShowAddFriends;
    this.timelineAnalytics = timelineAnalytics;
    this.storeId = storeId;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.timeline_follows_info;
  }

  CharSequence getFollowersText(Context context) {

    return spannableFactory.createSpan(
        context.getString(R.string.timeline_followers, getPojo().getData().getFollowers()),
        new ParcelableSpan[] { new ForegroundColorSpan(Color.BLACK) },
        String.valueOf(getPojo().getData().getFollowers()),
        String.valueOf(getPojo().getData().getFollowers()));
  }

  CharSequence getFollowingText(Context context) {

    return spannableFactory.createSpan(
        context.getString(R.string.timeline_followed, getPojo().getData().getFollowing()),
        new ParcelableSpan[] { new ForegroundColorSpan(Color.BLACK) },
        String.valueOf(getPojo().getData().getFollowing()),
        String.valueOf(getPojo().getData().getFollowing()));
  }

  public Void followersClick(FragmentNavigator navigator) {
    if (storeId > 0) {
      navigator.navigateTo(V8Engine.getFragmentProvider()
          .newTimeLineFollowersUsingStoreIdFragment(storeId, getPojo().getData().getFollowers(),
              storeTheme));
    } else if (userId != null && userId > 0) {
      navigator.navigateTo(V8Engine.getFragmentProvider()
          .newTimeLineFollowersUsingUserIdFragment(userId, getPojo().getData().getFollowers(),
              storeTheme));
    } else {
      navigator.navigateTo(V8Engine.getFragmentProvider()
          .newTimeLineFollowersFragment(getPojo().getData().getFollowers(), storeTheme));
    }
    return null;
  }

  public Void followingClick(FragmentNavigator navigator) {
    if (storeId > 0) {
      navigator.navigateTo(V8Engine.getFragmentProvider()
          .newTimeLineFollowingFragmentUsingStoreId(storeId, getPojo().getData().getFollowing(),
              storeTheme));
    } else {
      navigator.navigateTo(V8Engine.getFragmentProvider()
          .newTimeLineFollowingFragmentUsingUserId(userId, getPojo().getData().getFollowing(),
              storeTheme));
    }
    return null;
  }

  void followFriendsClick(FragmentNavigator navigator) {
    timelineAnalytics.sendFollowFriendsEvent();
    navigator.navigateTo(V8Engine.getFragmentProvider().newAddressBookFragment());
  }
}

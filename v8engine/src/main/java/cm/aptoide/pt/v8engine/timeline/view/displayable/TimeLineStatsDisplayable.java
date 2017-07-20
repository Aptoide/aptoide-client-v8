package cm.aptoide.pt.v8engine.timeline.view.displayable;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.ParcelableSpan;
import android.text.style.ForegroundColorSpan;
import cm.aptoide.pt.dataprovider.model.v7.TimelineStats;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.timeline.TimelineAnalytics;
import cm.aptoide.pt.v8engine.view.navigator.FragmentNavigator;
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
  private Resources resources;

  public TimeLineStatsDisplayable() {
  }

  public TimeLineStatsDisplayable(TimelineStats pojo, Long userId,
      SpannableFactory spannableFactory, String storeTheme, TimelineAnalytics timelineAnalytics,
      boolean shouldShowAddFriends, long storeId, Resources resources) {
    super(pojo);
    this.userId = userId;
    this.spannableFactory = spannableFactory;
    this.storeTheme = storeTheme;
    this.shouldShowAddFriends = shouldShowAddFriends;
    this.timelineAnalytics = timelineAnalytics;
    this.storeId = storeId;
    this.resources = resources;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.timeline_follows_info;
  }

  CharSequence getFollowersText(Context context) {

    return spannableFactory.createSpan(context.getString(R.string.timeline_button_followers,
        getPojo().getData()
            .getFollowers()), new ParcelableSpan[] { new ForegroundColorSpan(Color.BLACK) },
        String.valueOf(getPojo().getData()
            .getFollowers()), String.valueOf(getPojo().getData()
            .getFollowers()));
  }

  CharSequence getFollowingText(Context context) {

    return spannableFactory.createSpan(context.getString(R.string.timeline_button_followed,
        getPojo().getData()
            .getFollowing()), new ParcelableSpan[] { new ForegroundColorSpan(Color.BLACK) },
        String.valueOf(getPojo().getData()
            .getFollowing()), String.valueOf(getPojo().getData()
            .getFollowing()));
  }

  public Void followersClick(FragmentNavigator navigator) {
    if (storeId > 0) {
      navigator.navigateTo(V8Engine.getFragmentProvider()
          .newTimeLineFollowersUsingStoreIdFragment(storeId, storeTheme,
              AptoideUtils.StringU.getFormattedString(
                  R.string.social_timeline_followers_fragment_title, resources, getPojo().getData()
                      .getFollowers())));
    } else if (userId != null && userId > 0) {
      navigator.navigateTo(V8Engine.getFragmentProvider()
          .newTimeLineFollowersUsingUserIdFragment(userId, storeTheme,
              AptoideUtils.StringU.getFormattedString(
                  R.string.social_timeline_followers_fragment_title, resources, getPojo().getData()
                      .getFollowers())));
    } else {
      navigator.navigateTo(V8Engine.getFragmentProvider()
          .newTimeLineFollowersFragment(storeTheme, AptoideUtils.StringU.getFormattedString(
              R.string.social_timeline_followers_fragment_title, resources, getPojo().getData()
                  .getFollowers())));
    }
    return null;
  }

  public Void followingClick(FragmentNavigator navigator) {
    if (storeId > 0) {
      navigator.navigateTo(V8Engine.getFragmentProvider()
          .newTimeLineFollowingFragmentUsingStoreId(storeId, storeTheme,
              AptoideUtils.StringU.getFormattedString(
                  R.string.social_timeline_following_fragment_title, resources, getPojo().getData()
                      .getFollowing())));
    } else {
      navigator.navigateTo(V8Engine.getFragmentProvider()
          .newTimeLineFollowingFragmentUsingUserId(userId, storeTheme,
              AptoideUtils.StringU.getFormattedString(
                  R.string.social_timeline_following_fragment_title, resources, getPojo().getData()
                      .getFollowing())));
    }
    return null;
  }

  void followFriendsClick(FragmentNavigator navigator) {
    timelineAnalytics.sendFollowFriendsEvent();
    navigator.navigateTo(V8Engine.getFragmentProvider()
        .newAddressBookFragment());
  }
}

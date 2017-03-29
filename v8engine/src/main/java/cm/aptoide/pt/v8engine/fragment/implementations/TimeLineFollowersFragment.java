package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.GetFollowersRequest;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.model.v7.GetFollowers;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.FollowUserDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.MessageWhiteBgDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.listeners.EndlessRecyclerOnScrollListener;
import java.util.List;

/**
 * Created by trinkes on 10/03/2017.
 */

public class TimeLineFollowersFragment extends TimeLineFollowFragment {

  private Long userId;
  private Long storeId;
  private BodyInterceptor<BaseBody> baseBodyInterceptor;

  public static TimeLineFollowFragment newInstanceUsingUser(Long id, long followNumber,
      String storeTheme) {
    Bundle args = getBundle(followNumber, storeTheme);
    args.putLong(BundleKeys.USER_ID, id);
    TimeLineFollowersFragment fragment = new TimeLineFollowersFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @NonNull private static Bundle getBundle(long followNumber, String storeTheme) {
    Bundle args = new Bundle();
    args.putString(TITLE_KEY,
        AptoideUtils.StringU.getFormattedString(R.string.social_timeline_followers_fragment_title,
            followNumber));
    args.putString(BundleCons.STORE_THEME, storeTheme);
    return args;
  }

  public static TimeLineFollowFragment newInstanceUsingUser(long followNumber, String storeTheme) {
    Bundle args = getBundle(followNumber, storeTheme);
    TimeLineFollowersFragment fragment = new TimeLineFollowersFragment();
    fragment.setArguments(args);
    return fragment;
  }

  public static TimeLineFollowFragment newInstanceUsingStore(Long id, long followNumber,
      String storeTheme) {
    Bundle args = getBundle(followNumber, storeTheme);
    args.putLong(BundleKeys.STORE_ID, id);
    TimeLineFollowersFragment fragment = new TimeLineFollowersFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    baseBodyInterceptor = ((V8Engine) getContext().getApplicationContext())
        .getBaseBodyInterceptor();
  }

  @Override public void loadExtras(Bundle args) {
    super.loadExtras(args);
    if (args.containsKey(BundleKeys.USER_ID)) {
      userId = args.getLong(BundleKeys.USER_ID);
    }
    if (args.containsKey(BundleKeys.STORE_ID)) {
      storeId = args.getLong(BundleKeys.STORE_ID);
    }
  }

  @Override protected V7 buildRequest() {
    return GetFollowersRequest.of(baseBodyInterceptor, userId, storeId);
  }

  @Override protected Displayable createUserDisplayable(GetFollowers.TimelineUser user) {
    return new FollowUserDisplayable(user, false);
  }

  @Override EndlessRecyclerOnScrollListener.BooleanAction<GetFollowers> getFirstResponseAction(
      List<Displayable> dispList) {
    return response -> {
      dispList.add(0, new MessageWhiteBgDisplayable(getHeaderMessage()));
      return false;
    };
  }

  public String getFooterMessage(int hidden) {
    return getString(R.string.private_followers_message, hidden);
  }

  public String getHeaderMessage() {
    return getString(R.string.social_timeline_share_bar_followers);
  }
}

package cm.aptoide.pt.timeline.view.follow;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.GetFollowers;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.GetFollowersRequest;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.R;
import cm.aptoide.pt.V8Engine;
import cm.aptoide.pt.timeline.view.displayable.FollowUserDisplayable;
import cm.aptoide.pt.view.recycler.EndlessRecyclerOnScrollListener;
import cm.aptoide.pt.view.recycler.displayable.Displayable;
import cm.aptoide.pt.view.recycler.displayable.MessageWhiteBgDisplayable;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;

/**
 * Created by trinkes on 10/03/2017.
 */

public class TimeLineFollowersFragment extends TimeLineFollowFragment {

  private Long userId;
  private Long storeId;
  private BodyInterceptor<BaseBody> baseBodyInterceptor;
  private OkHttpClient httpClient;
  private Converter.Factory converterFactory;
  private TokenInvalidator tokenInvalidator;

  public static TimeLineFollowFragment newInstanceUsingUser(Long id, String storeTheme,
      String title) {
    Bundle args = getBundle(storeTheme, title);
    args.putLong(BundleKeys.USER_ID, id);
    TimeLineFollowersFragment fragment = new TimeLineFollowersFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @NonNull private static Bundle getBundle(String storeTheme, String title) {
    Bundle args = new Bundle();
    args.putString(TITLE_KEY, title);
    args.putString(BundleCons.STORE_THEME, storeTheme);
    return args;
  }

  public static TimeLineFollowFragment newInstanceUsingUser(String storeTheme, String title) {
    Bundle args = getBundle(storeTheme, title);
    TimeLineFollowersFragment fragment = new TimeLineFollowersFragment();
    fragment.setArguments(args);
    return fragment;
  }

  public static TimeLineFollowFragment newInstanceUsingStore(Long id, String storeTheme,
      String title) {
    Bundle args = getBundle(storeTheme, title);
    args.putLong(BundleKeys.STORE_ID, id);
    TimeLineFollowersFragment fragment = new TimeLineFollowersFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    baseBodyInterceptor =
        ((V8Engine) getContext().getApplicationContext()).getBaseBodyInterceptorV7();
    httpClient = ((V8Engine) getContext().getApplicationContext()).getDefaultClient();
    converterFactory = WebService.getDefaultConverter();
    tokenInvalidator = ((V8Engine) getContext().getApplicationContext()).getTokenInvalidator();
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
    return GetFollowersRequest.of(baseBodyInterceptor, userId, storeId, httpClient,
        converterFactory, tokenInvalidator,
        ((V8Engine) getContext().getApplicationContext()).getDefaultSharedPreferences());
  }

  @Override protected Displayable createUserDisplayable(GetFollowers.TimelineUser user) {
    return new FollowUserDisplayable(user, false);
  }

  @Override
  protected EndlessRecyclerOnScrollListener.BooleanAction<GetFollowers> getFirstResponseAction(
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

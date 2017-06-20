package cm.aptoide.pt.v8engine.timeline.view;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.GetUserLikesRequest;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.model.v7.GetFollowers;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.timeline.view.displayable.FollowUserDisplayable;
import cm.aptoide.pt.v8engine.timeline.view.follow.TimeLineFollowFragment;
import cm.aptoide.pt.v8engine.view.recycler.EndlessRecyclerOnScrollListener;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;

/**
 * Created by trinkes on 10/03/2017.
 */

public class TimeLineLikesFragment extends TimeLineFollowFragment {

  private String cardUid;
  private BodyInterceptor<BaseBody> baseBodyInterceptor;
  private OkHttpClient httpClient;
  private Converter.Factory converterFactory;
  private TokenInvalidator tokenInvalidator;

  public static TimeLineLikesFragment newInstance(String storeTheme, String cardUid,
      long numberOfLikes, String title) {
    Bundle args = new Bundle();
    args.putString(TITLE_KEY, title);
    args.putString(BundleCons.STORE_THEME, storeTheme);
    args.putString(BundleKeys.CARD_UID, cardUid);
    args.putLong(BundleKeys.NUMBER_LIKES, numberOfLikes);
    TimeLineLikesFragment fragment = new TimeLineLikesFragment();
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
    cardUid = args.getString(BundleKeys.CARD_UID);
  }

  @Override protected V7 buildRequest() {
    return GetUserLikesRequest.of(cardUid, baseBodyInterceptor, httpClient, converterFactory,
        tokenInvalidator, ((V8Engine) getContext().getApplicationContext()).getDefaultSharedPreferences());
  }

  @Override protected Displayable createUserDisplayable(GetFollowers.TimelineUser user) {
    return new FollowUserDisplayable(user, true);
  }

  @Override
  protected EndlessRecyclerOnScrollListener.BooleanAction<GetFollowers> getFirstResponseAction(
      List<Displayable> dispList) {
    return null;
  }

  @Override protected String getFooterMessage(int hidden) {
    return getString(R.string.social_timeline_users_private, hidden);
  }

  public String getHeaderMessage() {
    return "";
  }
}

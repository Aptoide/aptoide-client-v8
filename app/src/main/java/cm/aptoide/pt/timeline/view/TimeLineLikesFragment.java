package cm.aptoide.pt.timeline.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import cm.aptoide.pt.ApplicationPreferences;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.GetFollowers;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.GetUserLikesRequest;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.timeline.view.displayable.FollowUserDisplayable;
import cm.aptoide.pt.timeline.view.follow.TimeLineFollowFragment;
import cm.aptoide.pt.view.recycler.EndlessRecyclerOnScrollListener;
import cm.aptoide.pt.view.recycler.displayable.Displayable;
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
  private String defaultTheme;

  public static TimeLineLikesFragment newInstance(String storeTheme, String cardUid,
      long numberOfLikes, String title, StoreContext storeContext) {
    Bundle args = TimeLineFollowFragment.buildBundle(storeContext);
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
    final AptoideApplication application =
        (AptoideApplication) getContext().getApplicationContext();
    final ApplicationPreferences appPreferences = application.getApplicationPreferences();
    defaultTheme = appPreferences.getDefaultTheme();
    baseBodyInterceptor = application.getAccountSettingsBodyInterceptorPoolV7();
    httpClient = application.getDefaultClient();
    converterFactory = WebService.getDefaultConverter();
    tokenInvalidator = application.getTokenInvalidator();
  }

  @Override public void loadExtras(Bundle args) {
    super.loadExtras(args);
    cardUid = args.getString(BundleKeys.CARD_UID);
  }

  @Override protected V7 buildRequest() {
    return GetUserLikesRequest.of(cardUid, baseBodyInterceptor, httpClient, converterFactory,
        tokenInvalidator,
        ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences());
  }

  @Override protected Displayable createUserDisplayable(GetFollowers.TimelineUser user) {
    return new FollowUserDisplayable(user, true, defaultTheme);
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

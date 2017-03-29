package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.annotation.Nullable;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.GetUserLikesRequest;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.model.v7.GetFollowers;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.FollowUserDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.listeners.EndlessRecyclerOnScrollListener;
import java.util.List;

/**
 * Created by trinkes on 10/03/2017.
 */

public class TimeLineLikesFragment extends TimeLineFollowFragment {

  private String cardUid;
  private BodyInterceptor<BaseBody> baseBodyInterceptor;

  public static TimeLineLikesFragment newInstance(String storeTheme, String cardUid,
      long numberOfLikes) {
    Bundle args = new Bundle();
    args.putString(TITLE_KEY, DataProvider.getContext().getString(R.string.likes));
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
        ((V8Engine) getContext().getApplicationContext()).getBaseBodyInterceptor();
  }

  @Override public void loadExtras(Bundle args) {
    super.loadExtras(args);
    cardUid = args.getString(BundleKeys.CARD_UID);
  }

  @Override protected V7 buildRequest() {
    return GetUserLikesRequest.of(cardUid, baseBodyInterceptor);
  }

  @Override protected Displayable createUserDisplayable(GetFollowers.TimelineUser user) {
    return new FollowUserDisplayable(user, true);
  }

  @Override EndlessRecyclerOnScrollListener.BooleanAction<GetFollowers> getFirstResponseAction(
      List<Displayable> dispList) {
    return null;
  }

  public String getFooterMessage(int hidden) {
    return getString(R.string.social_timeline_users_private, hidden);
  }

  public String getHeaderMessage() {
    return "";
  }
}

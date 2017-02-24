package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.annotation.Partners;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.ws.v7.GetFollowersRequest;
import cm.aptoide.pt.dataprovider.ws.v7.GetFollowingRequest;
import cm.aptoide.pt.dataprovider.ws.v7.GetUserLikesRequest;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.model.v7.GetFollowers;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerSwipeWithToolbarFragment;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.FollowUserDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.MessageWhiteBgDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.listeners.EndlessRecyclerOnScrollListener;
import java.util.LinkedList;
import rx.functions.Action1;

/**
 * Created by trinkes on 16/12/2016.
 */

public class TimeLineFollowFragment extends GridRecyclerSwipeWithToolbarFragment {

  private AptoideClientUUID aptoideClientUUID;
  private EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;
  private TimeLineFollowFragment.FollowFragmentOpenMode openMode;
  @Nullable private String cardUid;
  @Nullable private Long numberOfLikes;
  private AptoideAccountManager accountManager;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    accountManager = ((V8Engine)getContext().getApplicationContext()).getAccountManager();
    aptoideClientUUID = new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
        DataProvider.getContext());
  }

  public static TimeLineFollowFragment newInstance(FollowFragmentOpenMode openMode,
      long followNumber, String storeTheme) {
    Bundle args = new Bundle();
    switch (openMode) {
      case FOLLOWERS:
        args.putString(TITLE_KEY, AptoideUtils.StringU.getFormattedString(
            R.string.social_timeline_followers_fragment_title, followNumber));
        break;
      case FOLLOWING:
        args.putString(TITLE_KEY, AptoideUtils.StringU.getFormattedString(
            R.string.social_timeline_following_fragment_title, followNumber));
        break;
    }
    args.putSerializable(BundleKeys.OPEN_MODE, openMode);
    args.putString(BundleCons.STORE_THEME, storeTheme);
    TimeLineFollowFragment fragment = new TimeLineFollowFragment();
    fragment.setArguments(args);
    return fragment;
  }

  public static TimeLineFollowFragment newInstance(FollowFragmentOpenMode openMode,
      String storeTheme, String cardUid, long numberOfLikes) {
    Bundle args = new Bundle();
    args.putString(TITLE_KEY, DataProvider.getContext().getString(R.string.likes));
    args.putSerializable(BundleKeys.OPEN_MODE, openMode);
    args.putString(BundleCons.STORE_THEME, storeTheme);
    args.putString(BundleKeys.CARD_UID, cardUid);
    args.putLong(BundleKeys.NUMBER_LIKES, numberOfLikes);
    TimeLineFollowFragment fragment = new TimeLineFollowFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override protected boolean displayHomeUpAsEnabled() {
    return true;
  }

  @Override public void loadExtras(Bundle args) {
    super.loadExtras(args);
    openMode = (FollowFragmentOpenMode) args.get(BundleKeys.OPEN_MODE);
    cardUid = (String) args.get(BundleKeys.CARD_UID);
    numberOfLikes = (Long) args.get(BundleKeys.NUMBER_LIKES);
  }

  @Override public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu_empty, menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int itemId = item.getItemId();
    if (itemId == android.R.id.home) {
      getActivity().onBackPressed();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override public void bindViews(View view) {
    super.bindViews(view);
    setHasOptionsMenu(true);
  }

  @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    super.load(create, refresh, savedInstanceState);
    if (create || refresh) {

      V7 request;
      switch (openMode) {
        case FOLLOWERS:
          request = GetFollowersRequest.of(accountManager.getAccessToken(),
              aptoideClientUUID.getUniqueIdentifier());
          break;
        case FOLLOWING:
          request = GetFollowingRequest.of(accountManager.getAccessToken(),
              aptoideClientUUID.getUniqueIdentifier());
          break;
        case LIKE_PREVIEW:
          request = GetUserLikesRequest.of(accountManager.getAccessToken(),
              new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
                  DataProvider.getContext()).getUniqueIdentifier(), cardUid);
          break;
        default:
          throw new IllegalStateException(
              "There is a case (openMode) that it is not being handled.");
      }
      LinkedList<Displayable> dispList = new LinkedList<>();

      final int[] hidden = { 0 };
      Action1<GetFollowers> action = (followersList) -> {
        hidden[0] += followersList.getDatalist().getHidden();
        for (GetFollowers.TimelineUser user : followersList.getDatalist().getList()) {
          dispList.add(new FollowUserDisplayable(user, openMode));
        }
        addDisplayables(dispList);
        dispList.clear();
      };

      EndlessRecyclerOnScrollListener.BooleanAction<GetFollowers> firstRequest = null;
      if (openMode == FollowFragmentOpenMode.FOLLOWERS
          || openMode == FollowFragmentOpenMode.FOLLOWING) {
        firstRequest = response -> {
          dispList.add(0, new MessageWhiteBgDisplayable(getHeaderMessage()));
          return false;
        };
      }

      getRecyclerView().clearOnScrollListeners();
      endlessRecyclerOnScrollListener =
          new EndlessRecyclerOnScrollListener(this.getAdapter(), request, action,
              (throwable) -> throwable.printStackTrace(), 6, true, firstRequest, null);
      endlessRecyclerOnScrollListener.setOnEndOfListReachedListener(
          () -> addDisplayable(new MessageWhiteBgDisplayable(getFooterMessage(hidden[0]))));
      getRecyclerView().addOnScrollListener(endlessRecyclerOnScrollListener);
      endlessRecyclerOnScrollListener.onLoadMore(refresh);
    } else {
      getRecyclerView().addOnScrollListener(endlessRecyclerOnScrollListener);
    }
  }

  public String getHeaderMessage() {
    String headerMessage;
    switch (openMode) {
      case FOLLOWERS:
        headerMessage = getString(R.string.social_timeline_share_bar_followers);
        break;
      case FOLLOWING:
        headerMessage = getString(R.string.social_timeline_share_bar_following);
        break;
      default:
        headerMessage = "";
    }
    return headerMessage;
  }

  public String getFooterMessage(int hidden) {
    String footerMessage;
    switch (openMode) {
      case FOLLOWERS:
        footerMessage = getString(R.string.private_followers_message, hidden);
        break;
      case FOLLOWING:
        footerMessage = getString(R.string.private_following_message, hidden);
        break;
      case LIKE_PREVIEW:
        footerMessage = getString(R.string.social_timeline_users_private, hidden);
        break;
      default:
        footerMessage = "";
    }
    return footerMessage;
  }

  @Override public void onDestroyView() {
    endlessRecyclerOnScrollListener.removeListeners();
    super.onDestroyView();
  }

  public enum FollowFragmentOpenMode {
    FOLLOWERS, FOLLOWING, LIKE_PREVIEW
  }

  @Partners public class BundleKeys {
    public static final String OPEN_MODE = "OPEN_MODE";
    public static final String CARD_UID = "CARDUID";
    public static final String NUMBER_LIKES = "NUMBER_LIKES";
  }
}

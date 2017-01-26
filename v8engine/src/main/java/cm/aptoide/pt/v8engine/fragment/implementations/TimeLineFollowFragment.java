package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.ws.v7.GetFollowersRequest;
import cm.aptoide.pt.dataprovider.ws.v7.GetFollowingRequest;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.model.v7.GetFollowers;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
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

  public static final String OPEN_MODE = "OPEN_MODE";
  private EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;
  private TimeLineFollowFragment.FollowFragmentOpenMode openMode;

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
    args.putSerializable(OPEN_MODE, openMode);
    args.putString(BundleCons.STORE_THEME, storeTheme);
    TimeLineFollowFragment fragment = new TimeLineFollowFragment();
    fragment.setArguments(args);
    return fragment;
  }

  public static TimeLineFollowFragment newInstance(FollowFragmentOpenMode openMode,
      String storeTheme) {
    Bundle args = new Bundle();
    args.putString(TITLE_KEY,
        DataProvider.getContext().getString(R.string.social_timeline_who_liked));
    args.putSerializable(OPEN_MODE, openMode);
    args.putString(BundleCons.STORE_THEME, storeTheme);
    TimeLineFollowFragment fragment = new TimeLineFollowFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override protected boolean displayHomeUpAsEnabled() {
    return true;
  }

  @Override public void loadExtras(Bundle args) {
    super.loadExtras(args);
    openMode = (FollowFragmentOpenMode) args.get(OPEN_MODE);
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

  @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    super.load(create, refresh, savedInstanceState);
    if (create || refresh) {

      V7 request;
      switch (openMode) {
        case FOLLOWERS:
          request = GetFollowersRequest.of(AptoideAccountManager.getAccessToken(),
              new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
                  DataProvider.getContext()).getAptoideClientUUID());
          break;
        case FOLLOWING:
          request = GetFollowingRequest.of(AptoideAccountManager.getAccessToken(),
              new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
                  DataProvider.getContext()).getAptoideClientUUID());
          break;
        case LIKE_PREVIEW:
          request = null;
          // TODO: 26/01/2017 CALL TO NEW WEBSERVICE GET USERS THAT LIKE
          break;
        default:
          throw new IllegalStateException("There is case (openMode) that it is not being handled.");
      }
      LinkedList<Displayable> dispList = new LinkedList<>();

      final int[] hidden = { 0 };
      Action1<GetFollowers> action = (followersList) -> {
        hidden[0] += followersList.getDatalist().getHidden();
        for (GetFollowers.TimelineUser user : followersList.getDatalist().getList()) {
          dispList.add(new FollowUserDisplayable(user));
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

      recyclerView.clearOnScrollListeners();
      endlessRecyclerOnScrollListener =
          new EndlessRecyclerOnScrollListener(this.getAdapter(), request, action,
              Throwable::printStackTrace, 6, true, firstRequest, null);
      if (openMode == FollowFragmentOpenMode.FOLLOWERS
          || openMode == FollowFragmentOpenMode.FOLLOWING) {
        endlessRecyclerOnScrollListener.setOnEndOfListReachedListener(
            () -> addDisplayable(new MessageWhiteBgDisplayable(getFooterMessage(hidden[0]))));
      }
      recyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);
      endlessRecyclerOnScrollListener.onLoadMore(refresh);
    } else {
      recyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);
    }
  }

  @Override public void onDestroyView() {
    endlessRecyclerOnScrollListener.removeListeners();
    super.onDestroyView();
  }

  @Override public void bindViews(View view) {
    super.bindViews(view);
    setHasOptionsMenu(true);
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
      default:
        footerMessage = "";
    }
    return footerMessage;
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

  public enum FollowFragmentOpenMode {
    FOLLOWERS, FOLLOWING, LIKE_PREVIEW
  }
}

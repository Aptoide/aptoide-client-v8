package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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

  @Override public void setupToolbar() {
    super.setupToolbar();
    if (toolbar != null) {
      ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();
      bar.setDisplayHomeAsUpEnabled(true);
    }
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
        default:
        case FOLLOWING:
          request = GetFollowingRequest.of(AptoideAccountManager.getAccessToken(),
              new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
                  DataProvider.getContext()).getAptoideClientUUID());
          break;
      }

      Action1<GetFollowers> action = (followersList) -> {
        LinkedList<Displayable> dispList = new LinkedList<>();
        switch (openMode) {
          case FOLLOWERS:
            dispList.add(new MessageWhiteBgDisplayable(
                getString(R.string.social_timeline_share_bar_followers)));
            break;
          case FOLLOWING:
            dispList.add(new MessageWhiteBgDisplayable(
                getString(R.string.social_timeline_share_bar_following)));
            break;
        }
        for (GetFollowers.TimelineUser user : followersList.getDatalist().getList()) {
          dispList.add(new FollowUserDisplayable(user));
        }
        addDisplayables(dispList);
        finishLoading();
      };
      recyclerView.clearOnScrollListeners();
      endlessRecyclerOnScrollListener =
          new EndlessRecyclerOnScrollListener(this.getAdapter(), request, action,
              errorRequestListener, true);
      recyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);
      endlessRecyclerOnScrollListener.onLoadMore(refresh);
    } else {
      recyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);
    }
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

  @Override public void loadExtras(Bundle args) {
    super.loadExtras(args);
    openMode = (FollowFragmentOpenMode) args.get(OPEN_MODE);
  }

  public static TimeLineFollowFragment newInstance(FollowFragmentOpenMode openMode,
      long followNumber) {
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
    TimeLineFollowFragment fragment = new TimeLineFollowFragment();
    fragment.setArguments(args);
    return fragment;
  }

  public enum FollowFragmentOpenMode {
    FOLLOWERS, FOLLOWING,
  }
}

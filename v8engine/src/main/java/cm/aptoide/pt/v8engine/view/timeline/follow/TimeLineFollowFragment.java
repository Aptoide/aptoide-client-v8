package cm.aptoide.pt.v8engine.view.timeline.follow;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import cm.aptoide.pt.annotation.Partners;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.model.v7.GetFollowers;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.fragment.GridRecyclerSwipeWithToolbarFragment;
import cm.aptoide.pt.v8engine.view.recycler.EndlessRecyclerOnScrollListener;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.MessageWhiteBgDisplayable;
import java.util.LinkedList;
import java.util.List;
import rx.functions.Action1;

/**
 * Created by trinkes on 16/12/2016.
 */

public abstract class TimeLineFollowFragment extends GridRecyclerSwipeWithToolbarFragment {

  private EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;

  @Override protected boolean displayHomeUpAsEnabled() {
    return true;
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

  @Override public void onDestroyView() {
    endlessRecyclerOnScrollListener.removeListeners();
    super.onDestroyView();
  }

  @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    super.load(create, refresh, savedInstanceState);
    if (create || refresh) {

      V7 request = buildRequest();
      LinkedList<Displayable> dispList = new LinkedList<>();

      final int[] hidden = { 0 };
      Action1<GetFollowers> action = (followersList) -> {
        hidden[0] += followersList.getDatalist()
            .getHidden();
        for (GetFollowers.TimelineUser user : followersList.getDatalist()
            .getList()) {
          dispList.add(createUserDisplayable(user));
        }
        addDisplayables(dispList);
        dispList.clear();
      };

      EndlessRecyclerOnScrollListener.BooleanAction<GetFollowers> firstRequest =
          getFirstResponseAction(dispList);
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

  abstract protected V7 buildRequest();

  protected abstract Displayable createUserDisplayable(GetFollowers.TimelineUser user);

  protected abstract EndlessRecyclerOnScrollListener.BooleanAction<GetFollowers> getFirstResponseAction(
      List<Displayable> dispList);

  protected abstract String getFooterMessage(int hidden);

  protected abstract String getHeaderMessage();

  @Partners public class BundleKeys {
    public static final String USER_ID = "user_id";
    public static final String CARD_UID = "CARDUID";
    public static final String NUMBER_LIKES = "NUMBER_LIKES";
    public static final String STORE_ID = "STORE_ID";
  }
}

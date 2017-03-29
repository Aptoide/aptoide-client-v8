package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.ListFullReviewsRequest;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.model.v7.FullReview;
import cm.aptoide.pt.model.v7.ListFullReviews;
import cm.aptoide.pt.preferences.secure.SecureCoderDecoder;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.v8engine.BaseBodyInterceptor;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerSwipeFragment;
import cm.aptoide.pt.v8engine.interfaces.StoreCredentialsProvider;
import cm.aptoide.pt.v8engine.preferences.AdultContent;
import cm.aptoide.pt.v8engine.preferences.Preferences;
import cm.aptoide.pt.v8engine.preferences.SecurePreferences;
import cm.aptoide.pt.v8engine.util.StoreCredentialsProviderImpl;
import cm.aptoide.pt.v8engine.util.StoreUtils;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.RowReviewDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.listeners.EndlessRecyclerOnScrollListener;
import java.util.LinkedList;
import java.util.List;
import rx.functions.Action1;

public class LatestReviewsFragment extends GridRecyclerSwipeFragment {
  // on v6, 50 was the limit
  private static final int REVIEWS_LIMIT = 25;
  private static final String STORE_ID = "storeId";

  private long storeId;
  private EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;
  private List<Displayable> displayables;
  private StoreCredentialsProvider storeCredentialsProvider;
  private BodyInterceptor<BaseBody> baseBodyInterceptor;

  public static LatestReviewsFragment newInstance(long storeId) {
    LatestReviewsFragment fragment = new LatestReviewsFragment();
    Bundle args = new Bundle();
    args.putLong(STORE_ID, storeId);
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    storeCredentialsProvider = new StoreCredentialsProviderImpl();
    baseBodyInterceptor = ((V8Engine)getContext().getApplicationContext()).getBaseBodyInterceptor();
  }

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

  @Override public void loadExtras(Bundle args) {
    super.loadExtras(args);
    this.storeId = args.getLong(STORE_ID, -1);
  }

  @Override public void bindViews(View view) {
    super.bindViews(view);
    setHasOptionsMenu(true);
  }

  @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    super.load(create, refresh, savedInstanceState);
    if (create || refresh) {
      ListFullReviewsRequest listFullReviewsRequest =
          ListFullReviewsRequest.of(storeId, REVIEWS_LIMIT, 0,
              StoreUtils.getStoreCredentials(storeId, storeCredentialsProvider),
              baseBodyInterceptor);
      Action1<ListFullReviews> listFullReviewsAction = listTopFullReviews -> {
        List<FullReview> reviews = listTopFullReviews.getDatalist().getList();
        displayables = new LinkedList<>();
        for (final FullReview review : reviews) {
          displayables.add(new RowReviewDisplayable(review));
        }
        addDisplayables(displayables);
      };

      getRecyclerView().clearOnScrollListeners();
      endlessRecyclerOnScrollListener =
          new EndlessRecyclerOnScrollListener(this.getAdapter(), listFullReviewsRequest,
              listFullReviewsAction, err -> err.printStackTrace(), true);
      getRecyclerView().addOnScrollListener(endlessRecyclerOnScrollListener);
      endlessRecyclerOnScrollListener.onLoadMore(refresh);
    } else {
      getRecyclerView().addOnScrollListener(endlessRecyclerOnScrollListener);
    }
  }
}

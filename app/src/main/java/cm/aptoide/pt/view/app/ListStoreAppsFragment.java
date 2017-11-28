package cm.aptoide.pt.view.app;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.BackButtonFragment;
import com.jakewharton.rxbinding.support.v7.widget.RxRecyclerView;
import com.jakewharton.rxbinding.view.RxView;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

/**
 * Created by trinkes on 17/10/2017.
 */

public class ListStoreAppsFragment extends BackButtonFragment implements ListStoreAppsView {

  public static final String STORE_ID = "cm.aptoide.pt.ListStoreAppsFragment.storeId";
  public static final int LOAD_THRESHOLD = 5;
  private static final String LIST_STATE_KEY = "cm.aptoide.pt.ListStoreAppsFragment.ListState";
  private ListStoreAppsAdapter adapter;
  private long storeId;
  private PublishSubject<Application> appClicks;
  private PublishSubject<Void> refreshEvent;
  private RecyclerView recyclerView;
  private GridLayoutManager layoutManager;
  private ProgressBar startingLoadingLayout;
  private SwipeRefreshLayout swipeRefreshLayout;
  private Parcelable listState;
  private View noNetworkErrorLayout;
  private View genericErrorLayout;
  private View retryButton;
  private View noNetworkRetryButton;

  public static Fragment newInstance(long storeId) {
    Bundle args = new Bundle();
    args.putLong(STORE_ID, storeId);
    Fragment fragment = new ListStoreAppsFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    storeId = getArguments().getLong(STORE_ID);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    if (savedInstanceState != null) {
      if (savedInstanceState.containsKey(LIST_STATE_KEY)) {
        listState = savedInstanceState.getParcelable(LIST_STATE_KEY);
        savedInstanceState.putParcelable(LIST_STATE_KEY, null);
      }
    }
    appClicks = PublishSubject.create();
    refreshEvent = PublishSubject.create();
    recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
    recyclerView.setVisibility(View.GONE);
    swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
    genericErrorLayout = view.findViewById(R.id.generic_error);
    noNetworkErrorLayout = view.findViewById(R.id.no_network_connection);
    retryButton = genericErrorLayout.findViewById(R.id.retry);
    noNetworkRetryButton = noNetworkErrorLayout.findViewById(R.id.retry);
    swipeRefreshLayout.setOnRefreshListener(() -> refreshEvent.onNext(null));
    setupToolbar(view);
    adapter = new ListStoreAppsAdapter(new ArrayList<>(), appClicks);
    recyclerView.setAdapter(adapter);
    int spanSize = getSpanSize(3);
    layoutManager = new GridLayoutManager(getContext(), spanSize);
    layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
      @Override public int getSpanSize(int position) {
        if (adapter != null && adapter.getItem(position) instanceof AppLoading) {
          return spanSize;
        }
        return 1;
      }
    });
    recyclerView.setLayoutManager(layoutManager);
    startingLoadingLayout = (ProgressBar) view.findViewById(R.id.progress_bar);
    startingLoadingLayout.setVisibility(View.VISIBLE);

    recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
      @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
          RecyclerView.State state) {
        if (getHost() != null) {
          int margin = AptoideUtils.ScreenU.getPixelsForDip(5, getResources());
          outRect.set(margin, margin, margin, margin);
        }
      }
    });

    int limit = spanSize * 10;
    attachPresenter(new ListStoreAppsPresenter(this, storeId, AndroidSchedulers.mainThread(),
        ((AptoideApplication) getContext().getApplicationContext()).getAppCenter(),
        CrashReport.getInstance(), getFragmentNavigator(), limit));
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  @Override public void onDestroyView() {
    listState = layoutManager.onSaveInstanceState();
    recyclerView = null;
    adapter = null;
    layoutManager.setSpanSizeLookup(new GridLayoutManager.DefaultSpanSizeLookup());
    layoutManager = null;
    startingLoadingLayout = null;
    swipeRefreshLayout = null;
    appClicks = null;
    refreshEvent = null;
    super.onDestroyView();
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    setHasOptionsMenu(true);
    return inflater.inflate(R.layout.list_store_apps_fragment_layout, container, false);
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if (adapter != null) {
      outState.putParcelable(LIST_STATE_KEY, layoutManager.onSaveInstanceState());
    }
  }

  @Override public void addApps(List<Application> appsList) {
    adapter.addApps(appsList);
    showApps();
  }

  @Override public Observable<Application> getAppClick() {
    return appClicks;
  }

  @Override public Observable<Object> reachesBottom() {
    return RxRecyclerView.scrollEvents(recyclerView)
        .distinctUntilChanged()
        .filter(scroll -> isEndReached())
        .cast(Object.class);
  }

  @Override public void hideLoading() {
    adapter.hideLoading();
  }

  @Override public void showLoading() {
    adapter.showLoading();
  }

  @Override public PublishSubject<Void> getRefreshEvent() {
    return refreshEvent;
  }

  @Override public void hideRefreshLoading() {
    swipeRefreshLayout.setRefreshing(false);
  }

  @Override public void setApps(List<Application> applications) {
    adapter.setApps(applications);
    if (listState != null) {
      layoutManager.onRestoreInstanceState(listState);
      listState = null;
    }
    showApps();
  }

  @Override public void showNetworkError() {
    noNetworkErrorLayout.setVisibility(View.VISIBLE);
    startingLoadingLayout.setVisibility(View.GONE);
    recyclerView.setVisibility(View.GONE);
    genericErrorLayout.setVisibility(View.GONE);
  }

  @Override public void showGenericError() {
    genericErrorLayout.setVisibility(View.VISIBLE);
    startingLoadingLayout.setVisibility(View.GONE);
    recyclerView.setVisibility(View.GONE);
    noNetworkErrorLayout.setVisibility(View.GONE);
  }

  @Override public Observable<Void> getRetryEvent() {
    return Observable.merge(RxView.clicks(retryButton), RxView.clicks(noNetworkRetryButton));
  }

  @Override public void showStartingLoading() {
    startingLoadingLayout.setVisibility(View.VISIBLE);
    recyclerView.setVisibility(View.GONE);
    genericErrorLayout.setVisibility(View.GONE);
    noNetworkErrorLayout.setVisibility(View.GONE);
  }

  private void showApps() {
    recyclerView.setVisibility(View.VISIBLE);
    startingLoadingLayout.setVisibility(View.GONE);
    genericErrorLayout.setVisibility(View.GONE);
    noNetworkErrorLayout.setVisibility(View.GONE);
  }

  private boolean isEndReached() {
    return layoutManager.getItemCount() - layoutManager.findLastVisibleItemPosition()
        <= LOAD_THRESHOLD;
  }

  public int getSpanSize(int defaultSpan) {
    return (int) (AptoideUtils.ScreenU.getScreenWidthInDip(
        (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE),
        getContext().getResources()) / AptoideUtils.ScreenU.REFERENCE_WIDTH_DPI * defaultSpan);
  }

  public void setupToolbar(View view) {
    Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
    ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
    toolbar.setTitle(R.string.list_store_apps_fragment_title);
    actionBar.setTitle(toolbar.getTitle());
  }
}

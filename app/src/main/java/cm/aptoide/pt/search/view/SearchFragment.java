package cm.aptoide.pt.search.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.AccessorFactory;
import cm.aptoide.pt.database.accessors.StoreAccessor;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.search.SearchApp;
import cm.aptoide.pt.dataprovider.util.HashMapNotNull;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.search.SearchAnalytics;
import cm.aptoide.pt.search.SearchManager;
import cm.aptoide.pt.search.SearchNavigator;
import cm.aptoide.pt.store.StoreUtils;
import cm.aptoide.pt.view.custom.DividerItemDecoration;
import cm.aptoide.pt.view.fragment.BaseToolbarFragment;
import cm.aptoide.pt.view.recycler.RecyclerViewPositionHelper;
import com.facebook.appevents.AppEventsLogger;
import com.jakewharton.rxbinding.support.v7.widget.RxRecyclerView;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxrelay.PublishRelay;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import org.parceler.Parcels;
import retrofit2.Converter;
import rx.Observable;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.Subscriptions;

public class SearchFragment extends BaseToolbarFragment implements SearchView {

  private static final int LAYOUT = R.layout.global_search_fragment;
  private static final String VIEW_MODEL = "view_model";

  private static final int VISIBLE_THRESHOLD = 5;

  private View noSearchLayout;
  private EditText noSearchLayoutSearchQuery;
  private ImageView noResultsSearchButton;
  private View searchResultsLayout;
  private View progressBar;
  private LinearLayout buttonsLayout;
  private Button followedStoresButton;
  private Button allStoresButton;

  private RecyclerViewPositionHelper followedStoresResultListPositionHelper;
  private boolean followedStoresBottomAlreadyReached = false;
  private RecyclerView followedStoresResultList;

  private RecyclerViewPositionHelper allStoresResultListPositionHelper;
  private boolean allStoresBottomAlreadyReached = false;
  private RecyclerView allStoresResultList;

  private SearchViewModel viewModel;
  private SearchResultAdapter allStoresResultAdapter;
  private SearchResultAdapter followedStoresResultAdapter;

  public static SearchFragment newInstance(String currentQuery) {
    return newInstance(currentQuery, false);
  }

  public static SearchFragment newInstance(String currentQuery, boolean onlyTrustedApps,
      String storeName) {

    SearchViewModel viewModel = new SearchViewModel(currentQuery, storeName, onlyTrustedApps);

    Bundle args = new Bundle();
    args.putParcelable(VIEW_MODEL, Parcels.wrap(viewModel));

    SearchFragment fragment = new SearchFragment();
    fragment.setArguments(args);
    return fragment;
  }

  public static SearchFragment newInstance(String currentQuery, boolean onlyTrustedApps) {

    SearchViewModel viewModel = new SearchViewModel(currentQuery, onlyTrustedApps);

    Bundle args = new Bundle();
    args.putParcelable(VIEW_MODEL, Parcels.wrap(viewModel));

    SearchFragment fragment = new SearchFragment();
    fragment.setArguments(args);
    return fragment;
  }

  public static SearchFragment newInstance(String currentQuery, String storeName) {

    SearchViewModel viewModel = new SearchViewModel(currentQuery, storeName);

    Bundle args = new Bundle();
    args.putParcelable(VIEW_MODEL, Parcels.wrap(viewModel));

    SearchFragment fragment = new SearchFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void showFollowedStoresResult() {
    followedStoresResultList.setVisibility(View.VISIBLE);
    followedStoresButton.setTextColor(getResources().getColor(R.color.white));
    followedStoresButton.setBackgroundResource(R.drawable.search_button_background);

    allStoresResultList.setVisibility(View.GONE);
    allStoresButton.setTextColor(getResources().getColor(R.color.silver_dark));
    allStoresButton.setBackgroundResource(0);
  }

  @Override public void showAllStoresResult() {
    followedStoresResultList.setVisibility(View.GONE);
    followedStoresButton.setTextColor(getResources().getColor(R.color.silver_dark));
    followedStoresButton.setBackgroundResource(0);

    allStoresResultList.setVisibility(View.VISIBLE);
    allStoresButton.setTextColor(getResources().getColor(R.color.white));
    allStoresButton.setBackgroundResource(R.drawable.search_button_background);
  }

  @Override public Observable<Void> clickFollowedStoresSearchButton() {
    return RxView.clicks(followedStoresButton)
        .doOnNext(__ -> viewModel.setAllStoresSelected(false));
  }

  @Override public Observable<Void> clickEverywhereSearchButton() {
    return RxView.clicks(allStoresButton)
        .doOnNext(__ -> viewModel.setAllStoresSelected(true));
  }

  @Override public Observable<String> clickNoResultsSearchButton() {
    return RxView.clicks(noResultsSearchButton)
        .map(__ -> noSearchLayoutSearchQuery.getText()
            .toString());
  }

  @Override public void showNoResultsImage() {
    noSearchLayout.setVisibility(View.VISIBLE);
    searchResultsLayout.setVisibility(View.GONE);
    buttonsLayout.setVisibility(View.GONE);
    followedStoresResultList.setVisibility(View.GONE);
    allStoresResultList.setVisibility(View.GONE);
  }

  @Override public void showResultsLayout() {
    noSearchLayout.setVisibility(View.GONE);
    searchResultsLayout.setVisibility(View.VISIBLE);
  }

  @Override public void showLoading() {
    followedStoresResultList.setVisibility(View.GONE);
    allStoresResultList.setVisibility(View.GONE);
    progressBar.setVisibility(View.VISIBLE);
  }

  @Override public void hideLoading() {
    progressBar.setVisibility(View.GONE);
    if (viewModel.isAllStoresSelected()) {
      allStoresResultList.setVisibility(View.VISIBLE);
    } else {
      followedStoresResultList.setVisibility(View.VISIBLE);
    }
  }

  @Override public void changeFollowedStoresButtonVisibility(boolean visible) {
    followedStoresButton.setVisibility(visible ? View.VISIBLE : View.GONE);
    if (visible) {
      buttonsLayout.setVisibility(View.VISIBLE);
    }
  }

  @Override public void changeAllStoresButtonVisibility(boolean visible) {
    allStoresButton.setVisibility(visible ? View.VISIBLE : View.GONE);
    if (visible) {
      buttonsLayout.setVisibility(View.VISIBLE);
    }
  }

  @Override public void addFollowedStoresResult(List<SearchApp> dataList) {
    followedStoresResultAdapter.addResultForSearch(dataList);
  }

  @Override public void addAllStoresResult(List<SearchApp> dataList) {
    allStoresResultAdapter.addResultForSearch(dataList);
  }

  @Override public Model getViewModel() {
    return viewModel;
  }

  @Override public void addFollowedStoresAdsResult(List<MinimalAd> ads) {
    followedStoresResultAdapter.addResultForAds(ads);
  }

  @Override public void addAllStoresAdsResult(List<MinimalAd> ads) {
    allStoresResultAdapter.addResultForAds(ads);
  }

  @Override public void setFollowedStoresAdsEmpty() {
    followedStoresResultAdapter.setAdsLoaded();
  }

  @Override public void setAllStoresAdsEmpty() {
    allStoresResultAdapter.setAdsLoaded();
  }

  @Override public Observable<Integer> showPopup(boolean hasVersions, View anchor) {
    return Observable.create(subscriber -> {
      final Context context = getContext();
      final PopupMenu popupMenu = new PopupMenu(context, anchor);

      MenuInflater inflater = popupMenu.getMenuInflater();
      inflater.inflate(R.menu.menu_search_item, popupMenu.getMenu());

      if (hasVersions) {
        MenuItem menuItemVersions = popupMenu.getMenu()
            .findItem(R.id.versions);
        menuItemVersions.setVisible(true);
      }

      popupMenu.setOnMenuItemClickListener(item -> {
        subscriber.onNext(item.getItemId());
        subscriber.onCompleted();
        return true;
      });

      popupMenu.setOnDismissListener(__ -> subscriber.onCompleted());

      subscriber.add(Subscriptions.create(() -> {
        popupMenu.setOnMenuItemClickListener(null);
        popupMenu.dismiss();
      }));

      popupMenu.show();
    });
  }

  @Override public Observable<Void> followedStoresResultReachedBottom() {
    return RxRecyclerView.scrollEvents(followedStoresResultList)
        .filter(event -> !followedStoresBottomAlreadyReached
            && followedStoresResultListPositionHelper.getItemCount() > VISIBLE_THRESHOLD
            && followedStoresResultListPositionHelper != null
            && event.view()
            .isAttachedToWindow()
            && (followedStoresResultListPositionHelper.getItemCount() - event.view()
            .getChildCount()) <= ((
            followedStoresResultListPositionHelper.findFirstVisibleItemPosition() == -1 ? 0
                : followedStoresResultListPositionHelper.findFirstVisibleItemPosition())
            + VISIBLE_THRESHOLD))
        .map(event -> null)
        .doOnNext(__ -> followedStoresBottomAlreadyReached = true)
        .cast(Void.class);
  }

  @Override public Observable<Void> allStoresResultReachedBottom() {
    return RxRecyclerView.scrollEvents(allStoresResultList)
        .filter(event -> !allStoresBottomAlreadyReached
            && allStoresResultListPositionHelper.getItemCount() > VISIBLE_THRESHOLD
            && allStoresResultListPositionHelper != null
            && event.view()
            .isAttachedToWindow()
            && (allStoresResultListPositionHelper.getItemCount() - event.view()
            .getChildCount()) <= ((
            allStoresResultListPositionHelper.findFirstVisibleItemPosition() == -1 ? 0
                : allStoresResultListPositionHelper.findFirstVisibleItemPosition())
            + VISIBLE_THRESHOLD))
        .map(event -> null)
        .doOnNext(__ -> allStoresBottomAlreadyReached = true)
        .cast(Void.class);
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelable(VIEW_MODEL, Parcels.wrap(viewModel));
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu_search_results, menu);

    SearchBuilder searchBuilder =
        new SearchBuilder(menu.findItem(R.id.action_search), getContext(), getSearchNavigator(),
            viewModel.getCurrentQuery());
    searchBuilder.validateAndAttachSearch();
  }

  @Override public String getDefaultTheme() {
    return super.getDefaultTheme();
  }

  @NonNull private SearchNavigator getSearchNavigator() {
    final SearchNavigator searchNavigator;
    if (viewModel.getStoreName() != null
        && viewModel.getStoreName()
        .length() > 0) {
      searchNavigator = new SearchNavigator(getFragmentNavigator(), viewModel.getStoreName());
    } else {
      searchNavigator = new SearchNavigator(getFragmentNavigator(), getDefaultStore());
    }
    return searchNavigator;
  }

  @Override public int getContentViewId() {
    return LAYOUT;
  }

  @Override public void setupViews() {
    super.setupViews();
    restoreSelectedTab();
    setHasOptionsMenu(true);
    attachPresenter(createPresenter(), null);
  }

  @Override protected boolean displayHomeUpAsEnabled() {
    return true;
  }

  @Override public void setupToolbarDetails(Toolbar toolbar) {
    toolbar.setTitle(viewModel.getCurrentQuery());
  }

  @Override public void bindViews(View view) {
    super.bindViews(view);
    followedStoresResultList = (RecyclerView) view.findViewById(R.id.followed_stores_result_list);
    allStoresResultList = (RecyclerView) view.findViewById(R.id.all_stores_result_list);
    followedStoresButton = (Button) view.findViewById(R.id.subscribed);
    allStoresButton = (Button) view.findViewById(R.id.everywhere);
    buttonsLayout = (LinearLayout) view.findViewById(R.id.buttons_layout);
    noSearchLayout = view.findViewById(R.id.no_search_results_layout);
    noSearchLayoutSearchQuery = (EditText) view.findViewById(R.id.search_text);
    noResultsSearchButton = (ImageView) view.findViewById(R.id.ic_search_button);
    searchResultsLayout = view.findViewById(R.id.search_results_layout);
    progressBar = view.findViewById(R.id.progress_bar);
  }

  @Override public void loadExtras(Bundle args) {
    if (args != null) {
      viewModel = Parcels.unwrap(args.getParcelable(VIEW_MODEL));
    } else {
      viewModel = Parcels.unwrap(getArguments().getParcelable(VIEW_MODEL));
    }
  }

  private void restoreSelectedTab() {
    if (viewModel.isAllStoresSelected()) {
      showAllStoresResult();
    } else {
      showFollowedStoresResult();
    }
  }

  private Presenter createPresenter() {
    final AptoideApplication applicationContext =
        (AptoideApplication) getContext().getApplicationContext();

    final SharedPreferences sharedPreferences = applicationContext.getDefaultSharedPreferences();

    final TokenInvalidator tokenInvalidator = applicationContext.getTokenInvalidator();

    final BodyInterceptor<BaseBody> bodyInterceptor =
        applicationContext.getAccountSettingsBodyInterceptorPoolV7();

    final OkHttpClient httpClient = applicationContext.getDefaultClient();

    final Converter.Factory converterFactory = WebService.getDefaultConverter();

    final SearchAnalytics searchAnalytics =
        new SearchAnalytics(Analytics.getInstance(), AppEventsLogger.newLogger(applicationContext));

    final StoreAccessor storeAccessor =
        AccessorFactory.getAccessorFor(applicationContext.getDatabase(), Store.class);
    final HashMapNotNull<String, List<String>> subscribedStoresAuthMap =
        StoreUtils.getSubscribedStoresAuthMap(storeAccessor);
    final List<Long> subscribedStoresIds = StoreUtils.getSubscribedStoresIds(storeAccessor);

    final SearchManager searchManager =
        new SearchManager(sharedPreferences, tokenInvalidator, bodyInterceptor, httpClient,
            converterFactory, subscribedStoresAuthMap, subscribedStoresIds);

    final CrashReport crashReport = CrashReport.getInstance();
    final Scheduler mainThreadScheduler = AndroidSchedulers.mainThread();
    final SearchNavigator navigator =
        new SearchNavigator(getFragmentNavigator(), getDefaultStore());

    final PublishRelay<SearchApp> onItemViewClickRelay = PublishRelay.create();
    final PublishRelay<Pair<SearchApp, View>> onOpenPopupMenuClickRelay = PublishRelay.create();
    final PublishRelay<MinimalAd> onAdClickRelay = PublishRelay.create();

    final List<MinimalAd> searchResultAdsFollowedStores = new ArrayList<>();
    final List<SearchApp> searchResultFollowedStores = new ArrayList<>();

    followedStoresResultAdapter =
        new SearchResultAdapter(onAdClickRelay, onItemViewClickRelay, onOpenPopupMenuClickRelay,
            searchResultAdsFollowedStores, searchResultFollowedStores);
    followedStoresResultList.setAdapter(followedStoresResultAdapter);
    followedStoresResultList.setLayoutManager(
        new LinearLayoutManager(getContext(), LinearLayout.VERTICAL, false));
    followedStoresResultListPositionHelper =
        RecyclerViewPositionHelper.createHelper(followedStoresResultList);

    float padding = getResources().getDimension(R.dimen.padding_very_very_small);

    followedStoresResultList.addItemDecoration(new DividerItemDecoration(getContext(), padding));

    final List<MinimalAd> searchResultAdsAllStores = new ArrayList<>();
    final List<SearchApp> searchResultAllStores = new ArrayList<>();

    allStoresResultAdapter =
        new SearchResultAdapter(onAdClickRelay, onItemViewClickRelay, onOpenPopupMenuClickRelay,
            searchResultAdsAllStores, searchResultAllStores);
    allStoresResultList.setAdapter(allStoresResultAdapter);
    allStoresResultList.setLayoutManager(
        new LinearLayoutManager(getContext(), LinearLayout.VERTICAL, false));
    allStoresResultList.addItemDecoration(new DividerItemDecoration(getContext(), padding));
    allStoresResultListPositionHelper =
        RecyclerViewPositionHelper.createHelper(allStoresResultList);

    return new SearchPresenter(this, searchAnalytics, navigator, crashReport, mainThreadScheduler,
        searchManager, onAdClickRelay, onItemViewClickRelay, onOpenPopupMenuClickRelay);
  }
}

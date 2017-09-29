package cm.aptoide.pt.search.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.ads.AdsRepository;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.AccessorFactory;
import cm.aptoide.pt.database.accessors.StoreAccessor;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.util.HashMapNotNull;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.search.SearchAnalytics;
import cm.aptoide.pt.search.SearchManager;
import cm.aptoide.pt.search.SearchNavigator;
import cm.aptoide.pt.search.model.SearchAdResult;
import cm.aptoide.pt.search.model.SearchAppResult;
import cm.aptoide.pt.store.StoreUtils;
import cm.aptoide.pt.view.BackButtonFragment;
import cm.aptoide.pt.view.custom.DividerItemDecoration;
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

public class SearchFragment extends BackButtonFragment implements SearchView {

  private static final int LAYOUT = R.layout.global_search_fragment;
  private static final String VIEW_MODEL = "view_model";

  private static final int VISIBLE_THRESHOLD = 4;
  private static final long ANIMATION_DURATION = 125L;
  private static final String ALL_STORES_SEARCH_LIST_STATE = "all_stores_search_list_state";
  private static final String FOLLOWED_STORES_SEARCH_LIST_STATE =
      "followed_stores_search_list_state";

  private View noSearchLayout;
  private EditText noSearchLayoutSearchQuery;
  private ImageView noResultsSearchButton;
  private View searchResultsLayout;
  private View progressBar;
  private LinearLayout buttonsLayout;
  private Button followedStoresButton;
  private Button allStoresButton;

  private RecyclerViewPositionHelper followedStoresResultListPositionHelper;
  private RecyclerView followedStoresResultList;

  private RecyclerViewPositionHelper allStoresResultListPositionHelper;
  private RecyclerView allStoresResultList;

  private SearchViewModel viewModel;
  private SearchResultAdapter allStoresResultAdapter;
  private SearchResultAdapter followedStoresResultAdapter;
  private Toolbar toolbar;
  private PublishRelay<SearchAppResult> onItemViewClickRelay;
  private PublishRelay<Pair<SearchAppResult, View>> onOpenPopupMenuClickRelay;
  private PublishRelay<SearchAdResult> onAdClickRelay;
  private SearchManager searchManager;
  private Scheduler mainThreadScheduler;
  private SearchNavigator searchNavigator;
  private CrashReport crashReport;
  private SearchAnalytics searchAnalytics;
  private float listItemPadding;

  public static SearchFragment newInstance(String currentQuery) {
    return newInstance(currentQuery, false);
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

  private void findChildViews(View view) {
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
    toolbar = (Toolbar) view.findViewById(R.id.toolbar);
  }

  @Override public void showFollowedStoresResult() {
    if (followedStoresResultList.getVisibility() == View.VISIBLE) {
      return;
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      int viewWidth = allStoresResultList.getWidth();

      followedStoresResultList.setTranslationX(-viewWidth);
      followedStoresResultList.setVisibility(View.VISIBLE);
      followedStoresResultList.animate()
          .translationXBy(viewWidth)
          .setDuration(ANIMATION_DURATION)
          .start();

      allStoresResultList.animate()
          .translationXBy(viewWidth)
          .setDuration(ANIMATION_DURATION)
          .withEndAction(() -> {
            allStoresResultList.setVisibility(View.INVISIBLE);
            setFollowedStoresButtonSelected();
          })
          .start();
    } else {
      followedStoresResultList.setVisibility(View.VISIBLE);
      allStoresResultList.setVisibility(View.INVISIBLE);
    }
  }

  @Override public void showAllStoresResult() {
    if (allStoresResultList.getVisibility() == View.VISIBLE) {
      return;
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      int viewWidth = followedStoresResultList.getWidth();

      followedStoresResultList.animate()
          .translationXBy(-viewWidth)
          .setDuration(ANIMATION_DURATION)
          .start();

      allStoresResultList.setTranslationX(viewWidth);
      allStoresResultList.setVisibility(View.VISIBLE);
      allStoresResultList.animate()
          .translationXBy(-viewWidth)
          .setDuration(ANIMATION_DURATION)
          .withEndAction(() -> {
            followedStoresResultList.setVisibility(View.INVISIBLE);
            setAllStoresButtonSelected();
          })
          .start();
    } else {
      followedStoresResultList.setVisibility(View.INVISIBLE);
      allStoresResultList.setVisibility(View.VISIBLE);
    }
  }

  @Override public Observable<Void> clickFollowedStoresSearchButton() {
    return RxView.clicks(followedStoresButton);
  }

  @Override public Observable<Void> clickEverywhereSearchButton() {
    return RxView.clicks(allStoresButton);
  }

  @Override public Observable<String> clickNoResultsSearchButton() {
    return RxView.clicks(noResultsSearchButton)
        .map(__ -> noSearchLayoutSearchQuery.getText()
            .toString());
  }

  @Override public void showNoResultsView() {
    noSearchLayout.setVisibility(View.VISIBLE);
    searchResultsLayout.setVisibility(View.GONE);
    buttonsLayout.setVisibility(View.GONE);
    followedStoresResultList.setVisibility(View.GONE);
    allStoresResultList.setVisibility(View.GONE);
  }

  @Override public void showResultsView() {
    noSearchLayout.setVisibility(View.GONE);
    searchResultsLayout.setVisibility(View.VISIBLE);
  }

  @Override public void showLoading() {
    progressBar.setVisibility(View.VISIBLE);
    noSearchLayout.setVisibility(View.GONE);
    searchResultsLayout.setVisibility(View.GONE);
  }

  @Override public void hideLoading() {
    progressBar.setVisibility(View.GONE);
  }

  @Override public void addFollowedStoresResult(List<SearchAppResult> dataList) {
    followedStoresResultAdapter.addResultForSearch(dataList);
  }

  @Override public void addAllStoresResult(List<SearchAppResult> dataList) {
    allStoresResultAdapter.addResultForSearch(dataList);
  }

  @Override public Model getViewModel() {
    return viewModel;
  }

  @Override public void setFollowedStoresAdsResult(SearchAdResult ad) {
    followedStoresResultAdapter.setResultForAd(ad);
  }

  @Override public void setAllStoresAdsResult(SearchAdResult ad) {
    allStoresResultAdapter.setResultForAd(ad);
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
        .filter(event -> followedStoresResultListPositionHelper.getItemCount() > VISIBLE_THRESHOLD
            && followedStoresResultListPositionHelper != null
            && event.view()
            .isAttachedToWindow()
            && (followedStoresResultListPositionHelper.getItemCount() - event.view()
            .getChildCount()) <= ((
            followedStoresResultListPositionHelper.findFirstVisibleItemPosition() == -1 ? 0
                : followedStoresResultListPositionHelper.findFirstVisibleItemPosition())
            + VISIBLE_THRESHOLD))
        .map(event -> null);
  }

  @Override public Observable<Void> allStoresResultReachedBottom() {
    return RxRecyclerView.scrollEvents(allStoresResultList)
        .filter(event -> allStoresResultListPositionHelper.getItemCount() > VISIBLE_THRESHOLD
            && allStoresResultListPositionHelper != null
            && event.view()
            .isAttachedToWindow()
            && (allStoresResultListPositionHelper.getItemCount() - event.view()
            .getChildCount()) <= ((
            allStoresResultListPositionHelper.findFirstVisibleItemPosition() == -1 ? 0
                : allStoresResultListPositionHelper.findFirstVisibleItemPosition())
            + VISIBLE_THRESHOLD))
        .map(event -> null);
  }

  @Override public void showLoadingMore() {
    allStoresResultAdapter.setIsLoadingMore(true);
    followedStoresResultAdapter.setIsLoadingMore(true);
  }

  @Override public void hideLoadingMore() {
    allStoresResultAdapter.setIsLoadingMore(false);
    followedStoresResultAdapter.setIsLoadingMore(false);
  }

  @Override public void setViewWithStoreNameAsSingleTab() {
    followedStoresButton.setText(viewModel.getStoreName());
    allStoresButton.setVisibility(View.GONE);
  }

  private void setFollowedStoresButtonSelected() {
    followedStoresButton.setTextColor(getResources().getColor(R.color.white));
    followedStoresButton.setBackgroundResource(R.drawable.search_button_background);
    allStoresButton.setTextColor(getResources().getColor(R.color.silver_dark));
    allStoresButton.setBackgroundResource(0);
    viewModel.setAllStoresSelected(false);
  }

  private void setAllStoresButtonSelected() {
    followedStoresButton.setTextColor(getResources().getColor(R.color.silver_dark));
    followedStoresButton.setBackgroundResource(0);
    allStoresButton.setTextColor(getResources().getColor(R.color.white));
    allStoresButton.setBackgroundResource(R.drawable.search_button_background);
    viewModel.setAllStoresSelected(true);
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    if (allStoresResultAdapter != null) {
      final Pair<List<SearchAppResult>, List<SearchAdResult>> allStoresState =
          allStoresResultAdapter.getState();
      viewModel.setAllStoresSearchAppResults(allStoresState.first);
      viewModel.setAllStoresSearchAdResults(allStoresState.second);
    }

    if (followedStoresResultAdapter != null) {
      final Pair<List<SearchAppResult>, List<SearchAdResult>> followedStoresState =
          followedStoresResultAdapter.getState();
      viewModel.setFollowedStoresSearchAppResults(followedStoresState.first);
      viewModel.setFollowedStoresSearchAdResults(followedStoresState.second);
    }

    outState.putParcelable(VIEW_MODEL, Parcels.wrap(viewModel));
    outState.putParcelable(ALL_STORES_SEARCH_LIST_STATE, allStoresResultList.getLayoutManager()
        .onSaveInstanceState());
    outState.putParcelable(FOLLOWED_STORES_SEARCH_LIST_STATE,
        followedStoresResultList.getLayoutManager()
            .onSaveInstanceState());
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
    final String defaultStore = getDefaultStore();
    if (viewModel.getStoreName() != null
        && viewModel.getStoreName()
        .length() > 0) {
      searchNavigator =
          new SearchNavigator(getFragmentNavigator(), viewModel.getStoreName(), defaultStore);
    } else {
      searchNavigator = new SearchNavigator(getFragmentNavigator(), defaultStore);
    }
    return searchNavigator;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    crashReport = CrashReport.getInstance();

    final AptoideApplication applicationContext =
        (AptoideApplication) getContext().getApplicationContext();

    final SharedPreferences sharedPreferences = applicationContext.getDefaultSharedPreferences();

    final TokenInvalidator tokenInvalidator = applicationContext.getTokenInvalidator();

    final BodyInterceptor<BaseBody> bodyInterceptor =
        applicationContext.getAccountSettingsBodyInterceptorPoolV7();

    final OkHttpClient httpClient = applicationContext.getDefaultClient();

    final Converter.Factory converterFactory = WebService.getDefaultConverter();

    searchAnalytics =
        new SearchAnalytics(Analytics.getInstance(), AppEventsLogger.newLogger(applicationContext));

    final StoreAccessor storeAccessor =
        AccessorFactory.getAccessorFor(applicationContext.getDatabase(), Store.class);
    final HashMapNotNull<String, List<String>> subscribedStoresAuthMap =
        StoreUtils.getSubscribedStoresAuthMap(storeAccessor);
    final List<Long> subscribedStoresIds = StoreUtils.getSubscribedStoresIds(storeAccessor);
    final AdsRepository adsRepository =
        ((AptoideApplication) getActivity().getApplication()).getAdsRepository();

    searchManager =
        new SearchManager(sharedPreferences, tokenInvalidator, bodyInterceptor, httpClient,
            converterFactory, subscribedStoresAuthMap, subscribedStoresIds, adsRepository);

    mainThreadScheduler = AndroidSchedulers.mainThread();
    searchNavigator = new SearchNavigator(getFragmentNavigator(), getDefaultStore());

    onItemViewClickRelay = PublishRelay.create();
    onOpenPopupMenuClickRelay = PublishRelay.create();
    onAdClickRelay = PublishRelay.create();

    final List<SearchAppResult> searchResultFollowedStores = new ArrayList<>();
    final List<SearchAdResult> searchResultAdsFollowedStores = new ArrayList<>();

    followedStoresResultAdapter =
        new SearchResultAdapter(onAdClickRelay, onItemViewClickRelay, onOpenPopupMenuClickRelay,
            searchResultFollowedStores, searchResultAdsFollowedStores, crashReport);

    listItemPadding = getResources().getDimension(R.dimen.padding_very_very_small);

    final List<SearchAppResult> searchResultAllStores = new ArrayList<>();
    final List<SearchAdResult> searchResultAdsAllStores = new ArrayList<>();

    allStoresResultAdapter =
        new SearchResultAdapter(onAdClickRelay, onItemViewClickRelay, onOpenPopupMenuClickRelay,
            searchResultAllStores, searchResultAdsAllStores, crashReport);
  }

  @NonNull private DividerItemDecoration getDefaultItemDecoration() {
    return new DividerItemDecoration(getContext(), listItemPadding);
  }

  @NonNull private LinearLayoutManager getDefaultLayoutManager() {
    return new LinearLayoutManager(getContext(), LinearLayout.VERTICAL, false);
  }

  @NonNull private SearchViewModel loadViewModel(Bundle arguments) {
    return Parcels.unwrap(arguments.getParcelable(VIEW_MODEL));
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    setHasOptionsMenu(true);
    return inflater.inflate(LAYOUT, container, false);
  }

  @Override public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
    super.onViewStateRestored(savedInstanceState);
    if (savedInstanceState != null) {
      if (savedInstanceState.containsKey(VIEW_MODEL)) {
        viewModel = loadViewModel(savedInstanceState);
      }

      if (allStoresResultAdapter != null) {
        allStoresResultAdapter.restoreState(viewModel.allStoresSearchAppResults,
            viewModel.allStoresSearchAdResults);
        allStoresResultAdapter.notifyDataSetChanged();
      }

      if (savedInstanceState.containsKey(ALL_STORES_SEARCH_LIST_STATE)
          && allStoresResultList != null) {

        RecyclerView.LayoutManager layoutManager = allStoresResultList.getLayoutManager();
        if (layoutManager == null) {
          layoutManager = getDefaultLayoutManager();
          allStoresResultList.setLayoutManager(layoutManager);
        }
        layoutManager.onRestoreInstanceState(
            savedInstanceState.getParcelable(ALL_STORES_SEARCH_LIST_STATE));
      }

      if (followedStoresResultAdapter != null) {
        followedStoresResultAdapter.restoreState(viewModel.followedStoresSearchAppResults,
            viewModel.followedStoresSearchAdResults);
        followedStoresResultAdapter.notifyDataSetChanged();
      }

      if (savedInstanceState.containsKey(FOLLOWED_STORES_SEARCH_LIST_STATE)
          && followedStoresResultList != null) {
        RecyclerView.LayoutManager layoutManager = followedStoresResultList.getLayoutManager();
        if (layoutManager == null) {
          layoutManager = getDefaultLayoutManager();
          followedStoresResultList.setLayoutManager(layoutManager);
        }
        layoutManager.onRestoreInstanceState(
            savedInstanceState.getParcelable(FOLLOWED_STORES_SEARCH_LIST_STATE));
      }
    }
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    findChildViews(view);

    viewModel = loadViewModel(getArguments());

    attachFollowedStoresResultListDependencies();
    followedStoresResultListPositionHelper =
        RecyclerViewPositionHelper.createHelper(followedStoresResultList);

    attachAllStoresResultListDependencies();
    allStoresResultListPositionHelper =
        RecyclerViewPositionHelper.createHelper(allStoresResultList);

    attachToolbar();

    attachPresenter(new SearchPresenter(this, searchAnalytics, searchNavigator, crashReport,
        mainThreadScheduler, searchManager, onAdClickRelay, onItemViewClickRelay,
        onOpenPopupMenuClickRelay), null);
  }

  private void attachFollowedStoresResultListDependencies() {
    followedStoresResultList.addItemDecoration(getDefaultItemDecoration());
    followedStoresResultList.setAdapter(followedStoresResultAdapter);
    followedStoresResultList.setLayoutManager(getDefaultLayoutManager());
  }

  private void attachAllStoresResultListDependencies() {
    allStoresResultList.setAdapter(allStoresResultAdapter);
    allStoresResultList.setLayoutManager(getDefaultLayoutManager());
    allStoresResultList.addItemDecoration(getDefaultItemDecoration());
  }

  private void attachToolbar() {
    ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
    toolbar.setTitle(viewModel.getCurrentQuery());
    actionBar.setTitle(toolbar.getTitle());
  }
}

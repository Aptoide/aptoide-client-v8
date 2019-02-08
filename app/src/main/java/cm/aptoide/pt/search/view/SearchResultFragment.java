package cm.aptoide.pt.search.view;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.R;
import cm.aptoide.pt.ads.MoPubBannerAdListener;
import cm.aptoide.pt.ads.MoPubNativeAdsListener;
import cm.aptoide.pt.bottomNavigation.BottomNavigationActivity;
import cm.aptoide.pt.bottomNavigation.BottomNavigationItem;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.search.model.SearchAdResult;
import cm.aptoide.pt.search.model.SearchAdResultWrapper;
import cm.aptoide.pt.search.model.SearchAppResult;
import cm.aptoide.pt.search.model.SearchAppResultWrapper;
import cm.aptoide.pt.search.model.SearchViewModel;
import cm.aptoide.pt.search.model.Suggestion;
import cm.aptoide.pt.search.suggestions.SearchQueryEvent;
import cm.aptoide.pt.store.StoreTheme;
import cm.aptoide.pt.view.BackButtonFragment;
import cm.aptoide.pt.view.ThemeUtils;
import cm.aptoide.pt.view.custom.DividerItemDecoration;
import com.jakewharton.rxbinding.support.v7.widget.RxRecyclerView;
import com.jakewharton.rxbinding.support.v7.widget.RxSearchView;
import com.jakewharton.rxbinding.support.v7.widget.RxToolbar;
import com.jakewharton.rxbinding.support.v7.widget.SearchViewQueryTextEvent;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxrelay.PublishRelay;
import com.mopub.mobileads.MoPubView;
import com.mopub.nativeads.MoPubRecyclerAdapter;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.RequestParameters;
import com.mopub.nativeads.ViewBinder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Named;
import org.parceler.Parcels;
import rx.Observable;
import rx.subjects.PublishSubject;

import static android.view.View.VISIBLE;

public class SearchResultFragment extends BackButtonFragment
    implements SearchResultView, SearchSuggestionsView {

  private static final int LAYOUT = R.layout.global_search_fragment;
  private static final BottomNavigationItem BOTTOM_NAVIGATION_ITEM = BottomNavigationItem.SEARCH;
  private static final String VIEW_MODEL = "view_model";
  private static final String FOCUS_IN_SEARCH = "focus_in_search";
  private static final int COMPLETION_THRESHOLD = 0;

  private static final int VISIBLE_THRESHOLD = 5;
  private static final long ANIMATION_DURATION = 125L;
  private static final String ALL_STORES_SEARCH_LIST_STATE = "all_stores_search_list_state";
  private static final String FOLLOWED_STORES_SEARCH_LIST_STATE =
      "followed_stores_search_list_state";
  private static final String TRENDING_LIST_STATE = "trending_list_state";
  private static final String UNSUBMITTED_QUERY = "unsubmitted_query";

  @Inject SearchResultPresenter searchResultPresenter;
  @Inject @Named("aptoide-theme") String theme;
  private View noSearchLayout;
  private EditText noSearchLayoutSearchQuery;
  private ImageView noResultsSearchButton;
  private View searchResultsLayout;
  private ProgressBar progressBar;
  private CardView allAndFollowedStoresButtonsLayout;
  private Button followedStoresButton;
  private Button allStoresButton;
  private RecyclerView followedStoresResultList;
  private RecyclerView allStoresResultList;
  private RecyclerView suggestionsResultList;
  private RecyclerView trendingResultList;
  private SearchViewModel viewModel;
  private SearchResultAdapter allStoresResultAdapter;
  private SearchResultAdapter followedStoresResultAdapter;
  private SearchSuggestionsAdapter searchSuggestionsAdapter;
  private SearchSuggestionsAdapter searchTrendingAdapter;
  private Toolbar toolbar;
  private PublishRelay<SearchAppResultWrapper> onItemViewClickRelay;
  private PublishRelay<SearchAdResultWrapper> onAdClickRelay;
  private PublishSubject<SearchQueryEvent> suggestionClickedPublishSubject;
  private PublishSubject<SearchQueryEvent> queryTextChangedPublisher;
  private float listItemPadding;
  private MenuItem searchMenuItem;
  private SearchView searchView;
  private String currentQuery;
  private PublishSubject<Void> searchSetupPublishSubject;
  private boolean focusInSearchBar;
  private ActionBar actionBar;
  private boolean noResults;
  private String unsubmittedQuery;
  private boolean isSearchExpanded;
  private BottomNavigationActivity bottomNavigationActivity;
  private MoPubView bannerAd;
  private PublishSubject<Boolean> showingSearchResultsView;
  private MoPubRecyclerAdapter moPubRecyclerAdapter;

  public static SearchResultFragment newInstance(String currentQuery) {
    return newInstance(currentQuery, false);
  }

  public static SearchResultFragment newInstance(boolean focusInSearchBar) {
    return newInstance("", false, focusInSearchBar);
  }

  public static SearchResultFragment newInstance(String currentQuery, boolean onlyTrustedApps) {
    SearchViewModel viewModel = new SearchViewModel(currentQuery, onlyTrustedApps);
    Bundle args = new Bundle();
    args.putParcelable(VIEW_MODEL, Parcels.wrap(viewModel));
    SearchResultFragment fragment = new SearchResultFragment();
    fragment.setArguments(args);
    return fragment;
  }

  public static SearchResultFragment newInstance(String currentQuery, boolean onlyTrustedApps,
      boolean focusInSearchBar) {
    SearchViewModel viewModel = new SearchViewModel(currentQuery, onlyTrustedApps);
    Bundle args = new Bundle();
    args.putParcelable(VIEW_MODEL, Parcels.wrap(viewModel));
    args.putBoolean(FOCUS_IN_SEARCH, focusInSearchBar);
    SearchResultFragment fragment = new SearchResultFragment();
    fragment.setArguments(args);
    return fragment;
  }

  public static SearchResultFragment newInstance(String currentQuery, String storeName,
      String storeTheme) {
    SearchViewModel viewModel = new SearchViewModel(currentQuery, storeName, storeTheme);
    Bundle args = new Bundle();
    args.putParcelable(VIEW_MODEL, Parcels.wrap(viewModel));
    SearchResultFragment fragment = new SearchResultFragment();
    fragment.setArguments(args);
    return fragment;
  }

  private void findChildViews(View view) {
    allAndFollowedStoresButtonsLayout = (CardView) view.findViewById(
        R.id.fragment_search_result_all_followed_stores_buttons_layout);
    allStoresResultList =
        (RecyclerView) view.findViewById(R.id.fragment_search_result_all_stores_app_list);

    suggestionsResultList = (RecyclerView) view.findViewById(R.id.suggestions_list);

    trendingResultList = (RecyclerView) view.findViewById(R.id.trending_list);

    followedStoresResultList =
        (RecyclerView) view.findViewById(R.id.fragment_search_result_followed_stores_app_list);
    allStoresButton = (Button) view.findViewById(R.id.fragment_search_result_all_stores_button);
    followedStoresButton =
        (Button) view.findViewById(R.id.fragment_search_result_followed_stores_button);

    searchResultsLayout = view.findViewById(R.id.fragment_search_result_layout);

    noSearchLayout = view.findViewById(R.id.no_search_results_layout);
    noSearchLayoutSearchQuery = (EditText) view.findViewById(R.id.search_text);
    noResultsSearchButton = (ImageView) view.findViewById(R.id.ic_search_button);
    progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
    toolbar = (Toolbar) view.findViewById(R.id.toolbar);

    bannerAd = view.findViewById(R.id.mopub_banner);
  }

  @Override public void showFollowedStoresResult() {
    if (followedStoresResultList.getVisibility() == View.VISIBLE) {
      setFollowedStoresButtonSelected();
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
      setFollowedStoresButtonSelected();
    }
  }

  @Override public void showAllStoresResult() {
    if (allStoresResultList.getVisibility() == View.VISIBLE) {
      setAllStoresButtonSelected();
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
      setAllStoresButtonSelected();
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
    allAndFollowedStoresButtonsLayout.setVisibility(View.GONE);
    followedStoresResultList.setVisibility(View.GONE);
    allStoresResultList.setVisibility(View.GONE);
    suggestionsResultList.setVisibility(View.GONE);
    trendingResultList.setVisibility(View.GONE);
    noResults = true;
    bannerAd.setVisibility(View.GONE);
  }

  @Override public void showResultsView() {
    noSearchLayout.setVisibility(View.GONE);
    suggestionsResultList.setVisibility(View.GONE);
    trendingResultList.setVisibility(View.GONE);
    searchResultsLayout.setVisibility(View.VISIBLE);
    showingSearchResultsView.onNext(true);
  }

  @Override public void showLoading() {
    progressBar.setVisibility(View.VISIBLE);
    noSearchLayout.setVisibility(View.GONE);
    searchResultsLayout.setVisibility(View.GONE);
    bannerAd.setVisibility(View.GONE);
  }

  @Override public void hideLoading() {
    progressBar.setVisibility(View.GONE);
  }

  @Override public void addFollowedStoresResult(List<SearchAppResult> dataList) {
    followedStoresResultAdapter.addResultForSearch(dataList);
    viewModel.addFollowedStoresSearchAppResults(dataList);
  }

  @Override public void addAllStoresResult(List<SearchAppResult> dataList) {
    allStoresResultAdapter.addResultForSearch(dataList);
    viewModel.addAllStoresSearchAppResults(dataList);
  }

  @Override public Model getViewModel() {
    return viewModel;
  }

  @Override public void setFollowedStoresAdsResult(SearchAdResult ad) {
    followedStoresResultAdapter.setResultForAd(ad);
    viewModel.addFollowedStoresSearchAdResults(Arrays.asList(ad));
  }

  @Override public void setAllStoresAdsResult(SearchAdResult ad) {
    allStoresResultAdapter.setResultForAd(ad);
    viewModel.addAllStoresSearchAdResults(Arrays.asList(ad));
  }

  @Override public void setFollowedStoresAdsEmpty() {
    followedStoresResultAdapter.setAdsLoaded();
  }

  @Override public void setAllStoresAdsEmpty() {
    allStoresResultAdapter.setAdsLoaded();
  }

  @Override public Observable<Void> followedStoresResultReachedBottom() {
    return recyclerViewReachedBottom(followedStoresResultList);
  }

  @Override public Observable<Void> allStoresResultReachedBottom() {
    return recyclerViewReachedBottom(allStoresResultList);
  }

  @Override public void showLoadingMore() {
    allStoresResultAdapter.setIsLoadingMore(true);
    followedStoresResultAdapter.setIsLoadingMore(true);
  }

  @Override public void hideLoadingMore() {
    allStoresResultAdapter.setIsLoadingMore(false);
    followedStoresResultAdapter.setIsLoadingMore(false);
  }

  @Override public void setViewWithStoreNameAsSingleTab(String storeName) {
    followedStoresButton.setText(storeName);
    allStoresButton.setVisibility(View.GONE);
  }

  @Override public void hideFollowedStoresTab() {
    allStoresButton.setVisibility(View.VISIBLE);
    allStoresResultList.setVisibility(View.VISIBLE);
    followedStoresButton.setVisibility(View.GONE);
    followedStoresResultList.setVisibility(View.GONE);
    setAllStoresButtonSelected();
    viewModel.setAllStoresSelected(true);
  }

  @Override public void hideNonFollowedStoresTab() {
    allStoresButton.setVisibility(View.GONE);
    allStoresResultList.setVisibility(View.GONE);
    followedStoresButton.setVisibility(View.VISIBLE);
    followedStoresResultList.setVisibility(View.VISIBLE);
    setFollowedStoresButtonSelected();
    viewModel.setAllStoresSelected(false);
  }

  @Override public Observable<Void> searchSetup() {
    return searchSetupPublishSubject;
  }

  @Override public void toggleSuggestionsView() {
    suggestionsResultList.setVisibility(View.VISIBLE);
    trendingResultList.setVisibility(View.GONE);
  }

  @Override public void toggleTrendingView() {
    suggestionsResultList.setVisibility(View.GONE);
    trendingResultList.setVisibility(View.VISIBLE);
  }

  @Override public void hideSuggestionsViews() {
    suggestionsResultList.setVisibility(View.GONE);
    trendingResultList.setVisibility(View.GONE);
  }

  @Override public boolean isSearchViewExpanded() {
    return searchMenuItem.isActionViewExpanded();
  }

  @Override public Observable<Pair<String, SearchQueryEvent>> listenToSuggestionClick() {
    return suggestionClickedPublishSubject.map(event -> new Pair<>(unsubmittedQuery, event));
  }

  @Override public Observable<Void> toolbarClick() {
    return RxView.clicks(toolbar);
  }

  @Override public Observable<MenuItem> searchMenuItemClick() {
    return RxToolbar.itemClicks(toolbar)
        .filter(item -> item.getItemId() == searchMenuItem.getItemId());
  }

  @Override public Observable<SearchAdResultWrapper> onAdClicked() {
    return onAdClickRelay;
  }

  @Override public Observable<SearchAppResultWrapper> onViewItemClicked() {
    return onItemViewClickRelay;
  }

  @Override public Observable<SearchViewQueryTextEvent> queryChanged() {
    return RxSearchView.queryTextChangeEvents(searchView);
  }

  @Override public void queryEvent(SearchViewQueryTextEvent event) {
    queryTextChangedPublisher.onNext(new SearchQueryEvent(event.queryText()
        .toString(), event.isSubmitted()));
  }

  @Override public boolean shouldFocusInSearchBar() {
    return focusInSearchBar;
  }

  @Override public void scrollToTop() {
    RecyclerView list;
    if (followedStoresResultList.getVisibility() == View.VISIBLE) {
      list = followedStoresResultList;
    } else {
      list = allStoresResultList;
    }
    LinearLayoutManager layoutManager = ((LinearLayoutManager) list.getLayoutManager());
    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
    if (lastVisibleItemPosition > 10) {
      list.scrollToPosition(10);
    }
    list.smoothScrollToPosition(0);
  }

  @Override public boolean hasResults() {
    return (allStoresResultAdapter.getItemCount() != 0
        || followedStoresResultAdapter.getItemCount() != 0)
        && !searchMenuItem.isActionViewExpanded();
  }

  @Override public void disableUpNavigation() {
    if (actionBar != null) {
      actionBar.setHomeButtonEnabled(false);
      actionBar.setDisplayHomeAsUpEnabled(false);
      actionBar.setDisplayShowHomeEnabled(false);
    }
  }

  @Override public boolean shouldHideUpNavigation() {
    return (allStoresResultAdapter.getItemCount() == 0
        || followedStoresResultAdapter.getItemCount() == 0)
        && noSearchLayout.getVisibility() != VISIBLE;
  }

  @Override public void setUnsubmittedQuery(String query) {
    unsubmittedQuery = query;
  }

  @Override public void clearUnsubmittedQuery() {
    unsubmittedQuery = "";
  }

  @Override public void setVisibilityOnRestore() {
    if (!focusInSearchBar) {
      if (hasSearchResults()) {
        showResultsView();
      } else {
        showSuggestionsView();
      }
    }
  }

  @Override public boolean shouldShowSuggestions() {
    return toolbar.getTitle()
        .equals(getResources().getString(R.string.search_hint_title));
  }

  @Override public void showBannerAd() {
    bannerAd.setBannerAdListener(new MoPubBannerAdListener());
    bannerAd.setAdUnitId(BuildConfig.MOPUB_BANNER_50_SEARCH_PLACEMENT_ID);
    bannerAd.setVisibility(VISIBLE);
    bannerAd.loadAd();
  }

  @Override public Observable<Boolean> showingSearchResultsView() {
    return showingSearchResultsView;
  }

  @Override public void showNativeAds(String query) {
    RequestParameters requestParameters = new RequestParameters.Builder().keywords(query)
        .build();
    if (Build.VERSION.SDK_INT >= 21) {
      moPubRecyclerAdapter.loadAds(BuildConfig.MOPUB_NATIVE_SEARCH_PLACEMENT_ID, requestParameters);
    }
  }

  public void showSuggestionsView() {
    if (searchView.getQuery()
        .toString()
        .isEmpty()) {
      noSearchLayout.setVisibility(View.GONE);
      searchResultsLayout.setVisibility(View.GONE);
      trendingResultList.setVisibility(View.VISIBLE);
      suggestionsResultList.setVisibility(View.GONE);
      bannerAd.setVisibility(View.GONE);
    } else {
      noSearchLayout.setVisibility(View.GONE);
      searchResultsLayout.setVisibility(View.GONE);
      suggestionsResultList.setVisibility(View.VISIBLE);
      trendingResultList.setVisibility(View.GONE);
      bannerAd.setVisibility(View.GONE);
    }
  }

  private void forceSuggestions() {
    noSearchLayout.setVisibility(View.GONE);
    searchResultsLayout.setVisibility(View.GONE);
    trendingResultList.setVisibility(View.VISIBLE);
    suggestionsResultList.setVisibility(View.GONE);
    bannerAd.setVisibility(View.GONE);
  }

  private Observable<Void> recyclerViewReachedBottom(RecyclerView recyclerView) {
    return RxRecyclerView.scrollEvents(recyclerView)
        .filter(event -> event.dy() > 4)
        .filter(event -> event.view()
            .isAttachedToWindow())
        .filter(event -> {
          final LinearLayoutManager layoutManager = (LinearLayoutManager) event.view()
              .getLayoutManager();
          final int visibleItemCount = layoutManager.getChildCount();
          final int totalItemCount = layoutManager.getItemCount();
          final int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();
          return (visibleItemCount + pastVisibleItems) >= (totalItemCount - VISIBLE_THRESHOLD);
        })
        .debounce(650, TimeUnit.MILLISECONDS)
        .map(event -> null);
  }

  private void setFollowedStoresButtonSelected() {
    if (followedStoresButton.getVisibility() == View.VISIBLE) {
      followedStoresButton.setTextColor(getResources().getColor(R.color.white));
      followedStoresButton.setBackgroundResource(StoreTheme.get(theme)
          .getRoundGradientButtonDrawable());
    }
    if (allStoresButton.getVisibility() == View.VISIBLE) {
      allStoresButton.setTextColor(getResources().getColor(R.color.silver_dark));
      allStoresButton.setBackgroundResource(R.drawable.disabled_search_button_background);
    }
    viewModel.setAllStoresSelected(false);
    String storeTheme = viewModel.getStoreTheme();
    if (storeThemeExists(storeTheme)) {
      followedStoresButton.setBackgroundResource(StoreTheme.get(storeTheme)
          .getRoundGradientButtonDrawable());
    }
  }

  private void setAllStoresButtonSelected() {
    if (followedStoresButton.getVisibility() == View.VISIBLE) {
      followedStoresButton.setTextColor(getResources().getColor(R.color.silver_dark));
      followedStoresButton.setBackgroundResource(R.drawable.disabled_search_button_background);
    }
    if (allStoresButton.getVisibility() == View.VISIBLE) {
      allStoresButton.setTextColor(getResources().getColor(R.color.white));
      allStoresButton.setBackgroundResource(StoreTheme.get(theme)
          .getRoundGradientButtonDrawable());
    }
    viewModel.setAllStoresSelected(true);
    String storeTheme = viewModel.getStoreTheme();
    if (storeThemeExists(storeTheme)) {
      allStoresButton.setBackgroundResource(StoreTheme.get(storeTheme)
          .getRoundGradientButtonDrawable());
    }
  }

  private boolean storeThemeExists(String storeTheme) {
    return (storeTheme != null && storeTheme.length() > 0);
  }

  private boolean hasSearchResults() {
    return allStoresResultAdapter.getItemCount() > 0
        || followedStoresResultAdapter.getItemCount() > 0;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
    CrashReport crashReport = CrashReport.getInstance();

    if (viewModel == null && savedInstanceState != null && savedInstanceState.containsKey(
        VIEW_MODEL)) {
      viewModel = Parcels.unwrap(savedInstanceState.getParcelable(VIEW_MODEL));
    } else if (viewModel == null && getArguments().containsKey(VIEW_MODEL)) {
      viewModel = Parcels.unwrap(getArguments().getParcelable(VIEW_MODEL));
    }

    if (savedInstanceState != null && savedInstanceState.containsKey(FOCUS_IN_SEARCH)) {
      focusInSearchBar = savedInstanceState.getBoolean(FOCUS_IN_SEARCH);
    } else if (getArguments().containsKey(FOCUS_IN_SEARCH) && savedInstanceState == null) {
      focusInSearchBar = getArguments().getBoolean(FOCUS_IN_SEARCH);
    }

    if (viewModel != null) currentQuery = viewModel.getCurrentQuery();

    final AptoideApplication application = (AptoideApplication) getActivity().getApplication();

    noResults = false;

    onItemViewClickRelay = PublishRelay.create();
    onAdClickRelay = PublishRelay.create();
    suggestionClickedPublishSubject = PublishSubject.create();
    searchSetupPublishSubject = PublishSubject.create();
    queryTextChangedPublisher = PublishSubject.create();

    showingSearchResultsView = PublishSubject.create();

    final List<SearchAppResult> searchResultFollowedStores = new ArrayList<>();
    final List<SearchAdResult> searchResultAdsFollowedStores = new ArrayList<>();

    followedStoresResultAdapter =
        new SearchResultAdapter(onAdClickRelay, onItemViewClickRelay, searchResultFollowedStores,
            searchResultAdsFollowedStores, crashReport);

    listItemPadding = getResources().getDimension(R.dimen.padding_tiny);

    final List<SearchAppResult> searchResultAllStores = new ArrayList<>();
    final List<SearchAdResult> searchResultAdsAllStores = new ArrayList<>();

    allStoresResultAdapter =
        new SearchResultAdapter(onAdClickRelay, onItemViewClickRelay, searchResultAllStores,
            searchResultAdsAllStores, crashReport);

    searchSuggestionsAdapter =
        new SearchSuggestionsAdapter(new ArrayList<>(), suggestionClickedPublishSubject);
    searchTrendingAdapter =
        new SearchSuggestionsAdapter(new ArrayList<>(), suggestionClickedPublishSubject);

    setHasOptionsMenu(true);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    if (bottomNavigationActivity != null) {
      bottomNavigationActivity.requestFocus(BOTTOM_NAVIGATION_ITEM);
    }
    findChildViews(view);
    attachFollowedStoresResultListDependencies();
    attachAllStoresResultListDependencies();
    setupToolbar();
    setupTheme();

    suggestionsResultList.setLayoutManager(new LinearLayoutManager(getContext()));
    trendingResultList.setLayoutManager(new LinearLayoutManager(getContext()));
    suggestionsResultList.setAdapter(searchSuggestionsAdapter);
    trendingResultList.setAdapter(searchTrendingAdapter);

    if (viewModel != null && viewModel.hasData()) {
      restoreViewState(savedInstanceState != null ? savedInstanceState.getParcelable(
          ALL_STORES_SEARCH_LIST_STATE) : null,
          savedInstanceState != null ? savedInstanceState.getParcelable(
              FOLLOWED_STORES_SEARCH_LIST_STATE) : null,
          savedInstanceState != null ? savedInstanceState.getParcelable(TRENDING_LIST_STATE)
              : null);
    }
    if (savedInstanceState != null) {
      unsubmittedQuery =
          savedInstanceState.containsKey(UNSUBMITTED_QUERY) ? savedInstanceState.getString(
              UNSUBMITTED_QUERY) : "";
    }

    attachPresenter(searchResultPresenter);
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  private void setupTheme() {
    if (viewModel != null && storeThemeExists(viewModel.getStoreTheme())) {
      String storeTheme = viewModel.getStoreTheme();
      ThemeUtils.setStoreTheme(getActivity(), storeTheme);
      ThemeUtils.setStatusBarThemeColor(getActivity(), storeTheme);
      toolbar.setBackgroundResource(StoreTheme.get(storeTheme)
          .getGradientDrawable());
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        Drawable wrapDrawable = DrawableCompat.wrap(progressBar.getIndeterminateDrawable());
        DrawableCompat.setTint(wrapDrawable, ContextCompat.getColor(getContext(),
            StoreTheme.get(storeTheme)
                .getPrimaryColor()));
        progressBar.setIndeterminateDrawable(DrawableCompat.unwrap(wrapDrawable));
      } else {
        progressBar.getIndeterminateDrawable()
            .setColorFilter(ContextCompat.getColor(getContext(), StoreTheme.get(storeTheme)
                .getPrimaryColor()), PorterDuff.Mode.SRC_IN);
      }
    }
  }

  private void setupDefaultTheme() {
    if (storeThemeExists(theme)) {
      ThemeUtils.setStoreTheme(getActivity(), theme);
      ThemeUtils.setStatusBarThemeColor(getActivity(), theme);
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        Drawable wrapDrawable = DrawableCompat.wrap(progressBar.getIndeterminateDrawable());
        DrawableCompat.setTint(wrapDrawable, ContextCompat.getColor(getContext(),
            StoreTheme.get(theme)
                .getPrimaryColor()));
        progressBar.setIndeterminateDrawable(DrawableCompat.unwrap(wrapDrawable));
      } else {
        progressBar.getIndeterminateDrawable()
            .setColorFilter(ContextCompat.getColor(getContext(), StoreTheme.get(theme)
                .getPrimaryColor()), PorterDuff.Mode.SRC_IN);
      }
    }
  }

  @Override public void onDestroyView() {
    allStoresResultList.clearAnimation();
    followedStoresResultList.clearAnimation();
    setupDefaultTheme();
    super.onDestroyView();
    if (bannerAd != null) {
      bannerAd.destroy();
      bannerAd = null;
    }
  }

  @Override public void onDetach() {
    bottomNavigationActivity = null;
    super.onDetach();
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);
    if (activity instanceof BottomNavigationActivity) {
      bottomNavigationActivity = ((BottomNavigationActivity) activity);
    }
  }

  @Override public void onDestroy() {
    super.onDestroy();
    showingSearchResultsView = null;
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.fragment_search_result, menu);

    searchMenuItem = menu.findItem(R.id.menu_item_search);
    searchView = (SearchView) searchMenuItem.getActionView();
    searchView.setMaxWidth(Integer.MAX_VALUE);
    AutoCompleteTextView autoCompleteTextView =
        (AutoCompleteTextView) searchView.findViewById(R.id.search_src_text);
    autoCompleteTextView.setThreshold(COMPLETION_THRESHOLD);
    MenuItemCompat.setOnActionExpandListener(searchMenuItem,
        new MenuItemCompat.OnActionExpandListener() {
          @Override public boolean onMenuItemActionExpand(MenuItem menuItem) {
            enableUpNavigation();
            isSearchExpanded = true;
            return true;
          }

          @Override public boolean onMenuItemActionCollapse(MenuItem menuItem) {
            if (hasSearchResults()) {
              showResultsView();
            } else if (noResults) {
              showNoResultsView();
            } else {
              forceSuggestions();
            }
            if (shouldHideUpNavigation()) disableUpNavigation();
            isSearchExpanded = false;
            return true;
          }
        });

    focusInSearchBar = currentQuery.isEmpty() && !noResults;

    searchSetupPublishSubject.onNext(null);
  }

  @NonNull private DividerItemDecoration getDefaultItemDecoration() {
    return new DividerItemDecoration(getContext(), listItemPadding);
  }

  @NonNull private LinearLayoutManager getDefaultLayoutManager() {
    return new LinearLayoutManager(getContext(), LinearLayout.VERTICAL, false);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(LAYOUT, container, false);
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    outState.putParcelable(VIEW_MODEL, Parcels.wrap(viewModel));

    outState.putParcelable(ALL_STORES_SEARCH_LIST_STATE, allStoresResultList.getLayoutManager()
        .onSaveInstanceState());

    outState.putString(UNSUBMITTED_QUERY, unsubmittedQuery);

    if (isSearchExpanded) outState.putBoolean(FOCUS_IN_SEARCH, true);

    outState.putParcelable(FOLLOWED_STORES_SEARCH_LIST_STATE,
        followedStoresResultList.getLayoutManager()
            .onSaveInstanceState());
  }

  private void restoreViewState(@Nullable Parcelable allStoresSearchListState,
      @Nullable Parcelable followedStoresSearchListState, Parcelable trendingListState) {

    final List<SearchAppResult> allStoresSearchAppResults =
        viewModel.getAllStoresSearchAppResults();
    if (allStoresSearchAppResults.size() > 0) {
      allStoresResultAdapter.restoreState(allStoresSearchAppResults,
          viewModel.getAllStoresSearchAdResults());
      allStoresResultAdapter.notifyDataSetChanged();
    }

    if (allStoresSearchListState != null) {

      RecyclerView.LayoutManager layoutManager = allStoresResultList.getLayoutManager();
      if (layoutManager == null) {
        layoutManager = getDefaultLayoutManager();
        allStoresResultList.setLayoutManager(layoutManager);
      }
      layoutManager.onRestoreInstanceState(allStoresSearchListState);
    }

    final List<SearchAppResult> followedStoresSearchAppResults =
        viewModel.getFollowedStoresSearchAppResults();
    if (followedStoresSearchAppResults.size() > 0) {
      followedStoresResultAdapter.restoreState(followedStoresSearchAppResults,
          viewModel.getFollowedStoresSearchAdResults());
      followedStoresResultAdapter.notifyDataSetChanged();
    }

    if (followedStoresSearchListState != null) {
      RecyclerView.LayoutManager layoutManager = followedStoresResultList.getLayoutManager();
      if (layoutManager == null) {
        layoutManager = getDefaultLayoutManager();
        followedStoresResultList.setLayoutManager(layoutManager);
      }
      layoutManager.onRestoreInstanceState(followedStoresSearchListState);
    }

    showResultsView();

    if (viewModel.isAllStoresSelected()) {
      showAllStoresResult();
    } else {
      showFollowedStoresResult();
    }
  }

  private void attachFollowedStoresResultListDependencies() {
    followedStoresResultList.addItemDecoration(getDefaultItemDecoration());
    followedStoresResultList.setAdapter(followedStoresResultAdapter);
    followedStoresResultList.setLayoutManager(getDefaultLayoutManager());
  }

  private void attachAllStoresResultListDependencies() {
    moPubRecyclerAdapter = new MoPubRecyclerAdapter(getActivity(), allStoresResultAdapter);
    moPubRecyclerAdapter.registerAdRenderer(getMoPubStaticNativeAdRenderer());
    moPubRecyclerAdapter.setAdLoadedListener(new MoPubNativeAdsListener());
    if (Build.VERSION.SDK_INT >= 21) {
      allStoresResultList.setAdapter(moPubRecyclerAdapter);
    } else {
      allStoresResultList.setAdapter(allStoresResultAdapter);
    }
    allStoresResultList.setLayoutManager(getDefaultLayoutManager());
    allStoresResultList.addItemDecoration(getDefaultItemDecoration());
  }

  @NonNull private MoPubStaticNativeAdRenderer getMoPubStaticNativeAdRenderer() {
    return new MoPubStaticNativeAdRenderer(getMoPubViewBinder());
  }

  @NonNull private ViewBinder getMoPubViewBinder() {
    return new ViewBinder.Builder(R.layout.search_ad).titleId(R.id.app_name)
        .iconImageId(R.id.app_icon)
        .build();
  }

  private void setupToolbar() {

    if (viewModel.getCurrentQuery()
        .isEmpty() && !noResults) {
      toolbar.setTitle(R.string.search_hint_title);
      toolbar.setTitleMarginStart(100);
    } else if (viewModel.getCurrentQuery()
        .isEmpty()) {
      toolbar.setTitle(R.string.search_hint_title);
    } else {
      toolbar.setTitle(viewModel.getCurrentQuery());
    }

    final AppCompatActivity activity = (AppCompatActivity) getActivity();
    activity.setSupportActionBar(toolbar);
    actionBar = activity.getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setTitle(toolbar.getTitle());
    }
  }

  public void enableUpNavigation() {
    if (actionBar != null) {
      actionBar.setHomeButtonEnabled(true);
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setDisplayShowHomeEnabled(true);
    }
  }

  @Override public Observable<SearchQueryEvent> onQueryTextChanged() {
    return queryTextChangedPublisher;
  }

  @Override public void collapseSearchBar(boolean shouldShowSuggestions) {
    if (searchMenuItem != null) searchMenuItem.collapseActionView();
    if (!hasResults()) {
      toolbar.setTitle(R.string.search_hint_title);
    }
  }

  @Override public String getCurrentQuery() {
    return currentQuery != null ? currentQuery : "";
  }

  @Override public void focusInSearchBar() {
    if (searchMenuItem != null) {
      searchMenuItem.expandActionView();
    }

    if (searchView != null && unsubmittedQuery != null) {
      searchView.setQuery(unsubmittedQuery, false);
    } else if (searchView != null && !getCurrentQuery().isEmpty()) {
      final String currentQuery = getCurrentQuery();
      searchView.setQuery(currentQuery, false);
    }

    showSuggestionsView();
  }

  @Override public void setTrendingList(List<Suggestion> trending) {
    searchTrendingAdapter.addSuggestions(trending);
  }

  @Override public void setSuggestionsList(List<String> suggestions) {
    searchSuggestionsAdapter.addSuggestionsFromString(suggestions);
  }

  @Override public void setTrendingCursor(List<String> trending) {
    //Not to be used in this fragment!
  }
}

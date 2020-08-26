package cm.aptoide.pt.search.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.util.Pair;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.aptoideviews.errors.ErrorView;
import cm.aptoide.aptoideviews.filters.Filter;
import cm.aptoide.aptoideviews.filters.FiltersView;
import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.R;
import cm.aptoide.pt.ads.MoPubBannerAdListener;
import cm.aptoide.pt.ads.MoPubNativeAdsListener;
import cm.aptoide.pt.app.view.screenshots.ScreenShotClickEvent;
import cm.aptoide.pt.bottomNavigation.BottomNavigationActivity;
import cm.aptoide.pt.bottomNavigation.BottomNavigationItem;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.download.view.DownloadClick;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.search.model.SearchAppResult;
import cm.aptoide.pt.search.model.SearchAppResultWrapper;
import cm.aptoide.pt.search.model.SearchFilterType;
import cm.aptoide.pt.search.model.SearchQueryModel;
import cm.aptoide.pt.search.model.SearchViewModel;
import cm.aptoide.pt.search.model.Suggestion;
import cm.aptoide.pt.search.suggestions.SearchQueryEvent;
import cm.aptoide.pt.themes.ThemeManager;
import cm.aptoide.pt.view.BackButtonFragment;
import cm.aptoide.pt.view.rx.RxAlertDialog;
import cm.aptoide.pt.view.settings.InputDialog;
import com.google.android.material.snackbar.Snackbar;
import com.jakewharton.rxbinding.support.v7.widget.RecyclerViewScrollEvent;
import com.jakewharton.rxbinding.support.v7.widget.RxRecyclerView;
import com.jakewharton.rxbinding.support.v7.widget.RxSearchView;
import com.jakewharton.rxbinding.support.v7.widget.RxToolbar;
import com.jakewharton.rxbinding.support.v7.widget.SearchViewQueryTextEvent;
import com.jakewharton.rxbinding.view.RxView;
import com.mopub.mobileads.MoPubView;
import com.mopub.nativeads.InMobiNativeAdRenderer;
import com.mopub.nativeads.MoPubRecyclerAdapter;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.RequestParameters;
import com.mopub.nativeads.ViewBinder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
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
  private static final int VISIBLE_THRESHOLD = 2;
  private static final String ALL_STORES_SEARCH_LIST_STATE = "all_stores_search_list_state";
  private static final String UNSUBMITTED_QUERY = "unsubmitted_query";
  @Inject SearchResultPresenter searchResultPresenter;
  @Inject ThemeManager themeManager;
  private DecimalFormat oneDecimalFormatter = new DecimalFormat("#.##");
  private View noSearchLayout;
  private SwitchCompat noSearchAdultContentSwitch;
  private View searchResultsLayout;
  private ProgressBar progressBar;
  private ProgressBar progressBarResults;
  private RecyclerView searchResultList;
  private RecyclerView suggestionsResultList;
  private RecyclerView trendingResultList;
  private SearchViewModel viewModel;
  private SearchResultAdapter searchResultsAdapter;
  private SearchSuggestionsAdapter searchSuggestionsAdapter;
  private SearchSuggestionsAdapter searchTrendingAdapter;
  private Toolbar toolbar;
  private PublishSubject<SearchAppResultWrapper> onItemViewClickSubject;
  private PublishSubject<DownloadClick> downloadClickPublishSubject;
  private PublishSubject<ScreenShotClickEvent> screenShotClick;
  private PublishSubject<SearchQueryEvent> suggestionClickedPublishSubject;
  private PublishSubject<SearchQueryEvent> queryTextChangedPublisher;
  private MenuItem searchMenuItem;
  private SearchView searchView;
  private String currentQuery;
  private PublishSubject<Void> searchSetupPublishSubject;
  private boolean focusInSearchBar;
  private ActionBar actionBar;
  private boolean noResults;
  private boolean networkError;
  private String unsubmittedQuery;
  private boolean isSearchExpanded;
  private BottomNavigationActivity bottomNavigationActivity;
  private MoPubView bannerAdBottom;
  private PublishSubject<Boolean> noResultsAdultContentSubject;
  private MoPubRecyclerAdapter moPubRecyclerAdapter;
  private ErrorView errorView;
  private RxAlertDialog enableAdultContentDialog;
  private InputDialog enableAdultContentDialogWithPin;
  private PublishSubject<Void> noResultsPublishSubject;

  private CardView filtersCardView;
  private FiltersView filtersView;

  public static SearchResultFragment newInstance(SearchQueryModel searchQueryModel) {
    return newInstance(searchQueryModel, false);
  }

  public static SearchResultFragment newInstance(boolean focusInSearchBar) {
    return newInstance(new SearchQueryModel(), false, focusInSearchBar);
  }

  public static SearchResultFragment newInstance(SearchQueryModel searchQueryModel,
      boolean onlyTrustedApps) {
    SearchViewModel viewModel = new SearchViewModel(searchQueryModel, onlyTrustedApps);
    Bundle args = new Bundle();
    args.putParcelable(VIEW_MODEL, Parcels.wrap(viewModel));
    SearchResultFragment fragment = new SearchResultFragment();
    fragment.setArguments(args);
    return fragment;
  }

  public static SearchResultFragment newInstance(SearchQueryModel searchQueryModel,
      boolean onlyTrustedApps, boolean focusInSearchBar) {
    SearchViewModel viewModel = new SearchViewModel(searchQueryModel, onlyTrustedApps);
    Bundle args = new Bundle();
    args.putParcelable(VIEW_MODEL, Parcels.wrap(viewModel));
    args.putBoolean(FOCUS_IN_SEARCH, focusInSearchBar);
    SearchResultFragment fragment = new SearchResultFragment();
    fragment.setArguments(args);
    return fragment;
  }

  public static SearchResultFragment newInstance(SearchQueryModel searchQueryModel,
      String storeName, String storeTheme) {
    SearchViewModel viewModel = new SearchViewModel(searchQueryModel, storeName, storeTheme);
    Bundle args = new Bundle();
    args.putParcelable(VIEW_MODEL, Parcels.wrap(viewModel));
    SearchResultFragment fragment = new SearchResultFragment();
    fragment.setArguments(args);
    return fragment;
  }

  private void findChildViews(View view) {
    filtersCardView = view.findViewById(R.id.filters_card_view);
    searchResultList = view.findViewById(R.id.fragment_search_result_all_stores_app_list);

    suggestionsResultList = view.findViewById(R.id.suggestions_list);

    trendingResultList = view.findViewById(R.id.trending_list);

    searchResultsLayout = view.findViewById(R.id.fragment_search_result_layout);

    noSearchLayout = view.findViewById(R.id.no_search_results_layout);
    noSearchAdultContentSwitch = view.findViewById(R.id.no_search_adult_switch);
    progressBar = view.findViewById(R.id.progress_bar);
    progressBarResults = view.findViewById(R.id.progress_bar_results);
    toolbar = view.findViewById(R.id.toolbar);

    bannerAdBottom = view.findViewById(R.id.mopub_banner);
    errorView = view.findViewById(R.id.error_view);
    filtersView = view.findViewById(R.id.filters_view);

    noSearchAdultContentSwitch.setOnClickListener(
        v -> noResultsAdultContentSubject.onNext(noSearchAdultContentSwitch.isChecked()));
  }

  @Override public Observable<Boolean> clickAdultContentSwitch() {
    return noResultsAdultContentSubject;
  }

  @Override public Observable<Void> retryClicked() {
    return errorView.retryClick();
  }

  @Override public void showNoResultsView() {
    noSearchLayout.setVisibility(View.VISIBLE);
    searchResultsLayout.setVisibility(View.VISIBLE);
    filtersCardView.setVisibility(View.VISIBLE);
    searchResultList.setVisibility(View.GONE);
    suggestionsResultList.setVisibility(View.GONE);
    trendingResultList.setVisibility(View.GONE);
    noResults = true;
    bannerAdBottom.setVisibility(View.GONE);
    noResultsPublishSubject.onNext(null);
  }

  @Override public void showResultsView() {
    noSearchLayout.setVisibility(View.GONE);
    errorView.setVisibility(View.GONE);
    suggestionsResultList.setVisibility(View.GONE);
    trendingResultList.setVisibility(View.GONE);
    searchResultList.setVisibility(VISIBLE);
    searchResultsLayout.setVisibility(View.VISIBLE);
  }

  @Override public void showLoading() {
    progressBar.setVisibility(View.VISIBLE);
    noSearchLayout.setVisibility(View.GONE);
    errorView.setVisibility(View.GONE);
    searchResultsLayout.setVisibility(View.GONE);
    bannerAdBottom.setVisibility(View.GONE);
  }

  @Override public void hideLoading() {
    progressBar.setVisibility(View.GONE);
  }

  @Override public void showResultsLoading() {
    errorView.setVisibility(View.GONE);
    noSearchLayout.setVisibility(View.GONE);

    LayoutAnimationController layoutAnimationController =
        AnimationUtils.loadLayoutAnimation(getContext(), R.anim.exit_list_apps_anim);
    layoutAnimationController.getAnimation()
        .setFillAfter(true);
    searchResultList.setLayoutAnimation(layoutAnimationController);
    searchResultList.scheduleLayoutAnimation();

    progressBarResults.setVisibility(VISIBLE);
    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_down_appear);
    animation.setFillAfter(true);
    progressBarResults.startAnimation(animation);
  }

  @Override public void addAllStoresResult(String query, List<SearchAppResult> searchAppResults,
      boolean isFreshResult, boolean hasMore) {
    if (searchAppResults.size() > 0) {
      hideLoading();
      showResultsView();
    }
    if (isFreshResult) {
      searchResultsAdapter.setResultForSearch(query, searchAppResults, hasMore);
      Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up_disappear);
      animation.setFillAfter(true);
      animation.setAnimationListener(new Animation.AnimationListener() {
        @Override public void onAnimationStart(Animation animation) {
        }

        @Override public void onAnimationEnd(Animation animation) {
          progressBarResults.setVisibility(View.GONE);
        }

        @Override public void onAnimationRepeat(Animation animation) {
        }
      });
      progressBarResults.startAnimation(animation);

      searchResultList.setLayoutAnimation(
          AnimationUtils.loadLayoutAnimation(getContext(), R.anim.list_apps_anim));
      searchResultList.scheduleLayoutAnimation();
    } else {
      searchResultsAdapter.addResultForSearch(query, searchAppResults, hasMore);
    }
    viewModel.setLoadedResults(true);
  }

  @Override public Model getViewModel() {
    return viewModel;
  }

  @Override public Observable<Void> searchResultsReachedBottom() {
    return recyclerViewReachedBottom(searchResultList);
  }

  @Override public Observable<Void> searchSetup() {
    return searchSetupPublishSubject;
  }

  @Override public void toggleSuggestionsView() {
    suggestionsResultList.setVisibility(View.VISIBLE);
    trendingResultList.setVisibility(View.GONE);
    noSearchLayout.setVisibility(View.GONE);
    errorView.setVisibility(View.GONE);
    searchResultList.setVisibility(View.GONE);
    searchResultsLayout.setVisibility(View.GONE);
  }

  @Override public void toggleTrendingView() {
    suggestionsResultList.setVisibility(View.GONE);
    trendingResultList.setVisibility(View.VISIBLE);
    noSearchLayout.setVisibility(View.GONE);
    errorView.setVisibility(View.GONE);
    searchResultList.setVisibility(View.GONE);
    searchResultsLayout.setVisibility(View.GONE);
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

  @Override public Observable<SearchAppResultWrapper> onViewItemClicked() {
    return onItemViewClickSubject;
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
    RecyclerView list = searchResultList;
    LinearLayoutManager layoutManager = ((LinearLayoutManager) list.getLayoutManager());
    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
    if (lastVisibleItemPosition > 10) {
      list.scrollToPosition(10);
    }
    list.smoothScrollToPosition(0);
  }

  @Override public boolean hasResults() {
    return searchResultsAdapter.getItemCount() != 0 && !searchMenuItem.isActionViewExpanded();
  }

  @Override public void disableUpNavigation() {
    if (actionBar != null) {
      actionBar.setHomeButtonEnabled(false);
      actionBar.setDisplayHomeAsUpEnabled(false);
      actionBar.setDisplayShowHomeEnabled(false);
    }
  }

  @Override public boolean shouldHideUpNavigation() {
    return searchResultsAdapter.getItemCount() == 0 && (noSearchLayout.getVisibility() != VISIBLE
        || errorView.getVisibility() != VISIBLE);
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
    bannerAdBottom.setBannerAdListener(new MoPubBannerAdListener());
    bannerAdBottom.setAdUnitId(BuildConfig.MOPUB_BANNER_50_SEARCH_V2_PLACEMENT_ID);
    bannerAdBottom.loadAd();
  }

  @Override public void showNativeAds(String query) {
    RequestParameters requestParameters = new RequestParameters.Builder().keywords(query)
        .build();
    if (Build.VERSION.SDK_INT >= 21) {
      moPubRecyclerAdapter.loadAds(BuildConfig.MOPUB_NATIVE_SEARCH_V2_PLACEMENT_ID,
          requestParameters);
    }
  }

  @Override public void showNoNetworkView() {
    errorView.setError(ErrorView.Error.NO_NETWORK);
    errorView.setVisibility(View.VISIBLE);
    noSearchLayout.setVisibility(View.GONE);
    searchResultsLayout.setVisibility(View.VISIBLE);
    filtersCardView.setVisibility(View.VISIBLE);
    searchResultList.setVisibility(View.GONE);
    suggestionsResultList.setVisibility(View.GONE);
    trendingResultList.setVisibility(View.GONE);
    networkError = true;
    noResults = true;
    bannerAdBottom.setVisibility(View.GONE);
  }

  @Override public void showGenericErrorView() {
    errorView.setError(ErrorView.Error.GENERIC);
    errorView.setVisibility(View.VISIBLE);
    noSearchLayout.setVisibility(View.GONE);
    searchResultsLayout.setVisibility(View.VISIBLE);
    filtersCardView.setVisibility(View.VISIBLE);
    searchResultList.setVisibility(View.GONE);
    suggestionsResultList.setVisibility(View.GONE);
    trendingResultList.setVisibility(View.GONE);
    bannerAdBottom.setVisibility(View.GONE);
    networkError = true;
    noResults = true;
  }

  @Override public void disableAdultContent() {
    noSearchAdultContentSwitch.setChecked(false);
  }

  @Override public void enableAdultContent() {
    noSearchAdultContentSwitch.setChecked(true);
  }

  @Override public void showAdultContentConfirmationDialog() {
    enableAdultContentDialog.show();
  }

  @Override public Observable<DialogInterface> adultContentDialogPositiveClick() {
    return enableAdultContentDialog.positiveClicks();
  }

  @Override public Observable<CharSequence> adultContentWithPinDialogPositiveClick() {
    return enableAdultContentDialogWithPin.positiveClicks();
  }

  @Override public void setAdultContentSwitch(Boolean adultContent) {
    noSearchAdultContentSwitch.setChecked(adultContent);
  }

  @Override public void showAdultContentConfirmationDialogWithPin() {
    enableAdultContentDialogWithPin.show();
  }

  @Override public Observable<DialogInterface> adultContentDialogNegativeClick() {
    return enableAdultContentDialog.negativeClicks();
  }

  @Override public Observable<DialogInterface> adultContentPinDialogNegativeClick() {
    return enableAdultContentDialogWithPin.negativeClicks();
  }

  @Override public void showWrongPinErrorMessage() {
    Snackbar.make(getView(), R.string.adult_pin_wrong, Snackbar.LENGTH_LONG);
    noSearchAdultContentSwitch.setChecked(false);
  }

  @Override public Observable<Void> viewHasNoResults() {
    return noResultsPublishSubject;
  }

  @Override public Observable<List<Filter>> filtersChangeEvents() {
    return filtersView.filtersChangedEvents()
        .doOnNext(filters -> viewModel.setFilters(filters));
  }

  @Override public Observable<ScreenShotClickEvent> getScreenshotClickEvent() {
    return screenShotClick;
  }

  public void showSuggestionsView() {
    if (searchView.getQuery()
        .toString()
        .isEmpty()) {
      noSearchLayout.setVisibility(View.GONE);
      errorView.setVisibility(View.GONE);
      searchResultsLayout.setVisibility(View.GONE);
      trendingResultList.setVisibility(View.VISIBLE);
      suggestionsResultList.setVisibility(View.GONE);
      bannerAdBottom.setVisibility(View.GONE);
    } else {
      noSearchLayout.setVisibility(View.GONE);
      errorView.setVisibility(View.GONE);
      searchResultsLayout.setVisibility(View.GONE);
      suggestionsResultList.setVisibility(View.VISIBLE);
      trendingResultList.setVisibility(View.GONE);
      bannerAdBottom.setVisibility(View.GONE);
    }
  }

  private void forceSuggestions() {
    noSearchLayout.setVisibility(View.GONE);
    errorView.setVisibility(View.GONE);
    searchResultsLayout.setVisibility(View.GONE);
    trendingResultList.setVisibility(View.VISIBLE);
    suggestionsResultList.setVisibility(View.GONE);
    if (bannerAdBottom != null) {
      bannerAdBottom.setVisibility(View.GONE);
    }
  }

  private Observable<Void> recyclerViewReachedBottom(RecyclerView recyclerView) {
    return RxRecyclerView.scrollEvents(recyclerView)
        .map(this::isEndReached)
        .doOnNext(end -> Logger.getInstance()
            .d("lol", "emitting reached end #1 " + end))
        .distinctUntilChanged()
        .filter(isEnd -> isEnd)
        .doOnNext(end -> Logger.getInstance()
            .d("lol", "emitting reached end #2 " + end))
        .map(__ -> null);
  }

  private boolean isEndReached(RecyclerViewScrollEvent event) {
    final LinearLayoutManager layoutManager = (LinearLayoutManager) event.view()
        .getLayoutManager();
    return layoutManager.getItemCount() - layoutManager.findLastVisibleItemPosition()
        <= VISIBLE_THRESHOLD;
  }

  private boolean storeThemeExists(String storeTheme) {
    return (storeTheme != null && storeTheme.length() > 0);
  }

  private boolean hasSearchResults() {
    return searchResultsAdapter.getItemCount() > 0;
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

    if (viewModel != null) {
      currentQuery = viewModel.getSearchQueryModel()
          .getFinalQuery();
    }

    noResults = false;
    networkError = false;

    onItemViewClickSubject = PublishSubject.create();
    downloadClickPublishSubject = PublishSubject.create();
    screenShotClick = PublishSubject.create();
    suggestionClickedPublishSubject = PublishSubject.create();
    searchSetupPublishSubject = PublishSubject.create();
    queryTextChangedPublisher = PublishSubject.create();

    noResultsAdultContentSubject = PublishSubject.create();
    noResultsPublishSubject = PublishSubject.create();

    searchResultsAdapter =
        new SearchResultAdapter(onItemViewClickSubject, downloadClickPublishSubject,
            screenShotClick, new ArrayList<>(), crashReport);

    searchResultsAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
      @Override public void onItemRangeInserted(int positionStart, int itemCount) {
        if (positionStart == 0) {
          RecyclerView.LayoutManager layoutManager = searchResultList.getLayoutManager();
          if (layoutManager != null) {
            layoutManager.scrollToPosition(0);
          }
        }
      }
    });
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
    attachAllStoresResultListDependencies();
    setupToolbar();
    setupTheme();
    setupFilters();

    searchResultList.setItemAnimator(new DefaultItemAnimator() {
      @Override
      public boolean canReuseUpdatedViewHolder(@NonNull RecyclerView.ViewHolder viewHolder) {
        return true;
      }
    });

    suggestionsResultList.setLayoutManager(new LinearLayoutManager(getContext()));
    trendingResultList.setLayoutManager(new LinearLayoutManager(getContext()));
    suggestionsResultList.setAdapter(searchSuggestionsAdapter);
    trendingResultList.setAdapter(searchTrendingAdapter);

    if (viewModel != null) {
      restoreViewState(savedInstanceState != null ? savedInstanceState.getParcelable(
          ALL_STORES_SEARCH_LIST_STATE) : null);
    }
    if (viewModel != null) {
      if (viewModel.getFilters() != null) {
        filtersView.setFilters(viewModel.getFilters());
      } else {
        viewModel.setFilters(filtersView.getFilters());
      }
    }
    if (savedInstanceState != null) {
      unsubmittedQuery =
          savedInstanceState.containsKey(UNSUBMITTED_QUERY) ? savedInstanceState.getString(
              UNSUBMITTED_QUERY) : "";
    }

    enableAdultContentDialog =
        new RxAlertDialog.Builder(getContext(), themeManager).setMessage(R.string.are_you_adult)
            .setPositiveButton(R.string.yes)
            .setNegativeButton(R.string.no)
            .build();
    enableAdultContentDialogWithPin =
        new InputDialog.Builder(getContext(), themeManager).setMessage(R.string.request_adult_pin)
            .setPositiveButton(R.string.all_button_ok)
            .setNegativeButton(R.string.cancel)
            .setView(R.layout.dialog_request_input)
            .setEditText(R.id.input)
            .build();

    attachPresenter(searchResultPresenter);
  }

  private void setupFilters() {
    final List<Filter> filters;
    if (viewModel != null && viewModel.getStoreName() != null && !viewModel.getStoreName()
        .isEmpty()) {
      filters = Arrays.asList(new Filter(getString(R.string.search_filters_trusted), false,
              SearchFilterType.TRUSTED.name()),
          new Filter(getString(R.string.search_filters_beta), false, SearchFilterType.BETA.name()),
          new Filter(getString(R.string.search_filters_appcoins), false,
              SearchFilterType.APPC.name()));
    } else {
      filters = Arrays.asList(new Filter(getString(R.string.search_filters_followed_stores), false,
              SearchFilterType.FOLLOWED_STORES.name()),
          new Filter(getString(R.string.search_filters_trusted), false,
              SearchFilterType.TRUSTED.name()),
          new Filter(getString(R.string.search_filters_beta), false, SearchFilterType.BETA.name()),
          new Filter(getString(R.string.search_filters_appcoins), false,
              SearchFilterType.APPC.name()));
    }

    filtersView.setFilters(filters);
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  private void setupTheme() {
    if (viewModel != null && storeThemeExists(viewModel.getStoreTheme())) {
      String storeTheme = viewModel.getStoreTheme();
      themeManager.setTheme(storeTheme);
      toolbar.setBackgroundResource(
          themeManager.getAttributeForTheme(storeTheme, R.attr.toolbarBackground).resourceId);
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        Drawable wrapDrawable = DrawableCompat.wrap(progressBar.getIndeterminateDrawable());
        DrawableCompat.setTint(wrapDrawable,
            themeManager.getAttributeForTheme(R.attr.colorPrimary).data);
        progressBar.setIndeterminateDrawable(DrawableCompat.unwrap(wrapDrawable));
      } else {
        progressBar.getIndeterminateDrawable()
            .setColorFilter(themeManager.getAttributeForTheme(R.attr.colorPrimary).data,
                PorterDuff.Mode.SRC_IN);
      }
    }
  }

  @Override public Observable<DownloadClick> getDownloadClickEvents() {
    return downloadClickPublishSubject;
  }

  private void setupDefaultTheme() {
    themeManager.resetToBaseTheme();
  }

  @Override public void onDestroyView() {
    searchResultList.clearAnimation();
    setupDefaultTheme();
    super.onDestroyView();
    if (moPubRecyclerAdapter != null) {
      moPubRecyclerAdapter.destroy();
      moPubRecyclerAdapter = null;
    }
    if (bannerAdBottom != null) {
      bannerAdBottom.destroy();
      bannerAdBottom = null;
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
    noResultsAdultContentSubject = null;
    noResultsPublishSubject = null;
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.fragment_search_result, menu);

    searchMenuItem = menu.findItem(R.id.menu_item_search);
    searchView = (SearchView) searchMenuItem.getActionView();
    searchView.setMaxWidth(Integer.MAX_VALUE);
    AutoCompleteTextView autoCompleteTextView = searchView.findViewById(R.id.search_src_text);
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
            } else if (networkError) {
              showNoNetworkView();
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

  @NonNull private LinearLayoutManager getDefaultLayoutManager() {
    return new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(LAYOUT, container, false);
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    outState.putParcelable(VIEW_MODEL, Parcels.wrap(viewModel));

    if (searchResultList != null && searchResultList.getLayoutManager() != null) {
      outState.putParcelable(ALL_STORES_SEARCH_LIST_STATE, searchResultList.getLayoutManager()
          .onSaveInstanceState());
    }

    outState.putString(UNSUBMITTED_QUERY, unsubmittedQuery);

    if (isSearchExpanded) outState.putBoolean(FOCUS_IN_SEARCH, true);
  }

  private void restoreViewState(@Nullable Parcelable allStoresSearchListState) {
    if (allStoresSearchListState != null) {

      RecyclerView.LayoutManager layoutManager = searchResultList.getLayoutManager();
      if (layoutManager == null) {
        layoutManager = getDefaultLayoutManager();
        searchResultList.setLayoutManager(layoutManager);
      }
      layoutManager.onRestoreInstanceState(allStoresSearchListState);
    }
    showResultsView();
  }

  private void attachAllStoresResultListDependencies() {
    moPubRecyclerAdapter = new MoPubRecyclerAdapter(getActivity(), searchResultsAdapter);
    configureAdRenderers();
    moPubRecyclerAdapter.setAdLoadedListener(new MoPubNativeAdsListener());
    if (Build.VERSION.SDK_INT >= 21) {
      searchResultList.setAdapter(moPubRecyclerAdapter);
    } else {
      searchResultList.setAdapter(searchResultsAdapter);
    }
    searchResultList.setLayoutManager(getDefaultLayoutManager());
  }

  public void configureAdRenderers() {
    ViewBinder viewBinder = getMoPubViewBinder();
    moPubRecyclerAdapter.registerAdRenderer(new MoPubStaticNativeAdRenderer(viewBinder));
    moPubRecyclerAdapter.registerAdRenderer(new InMobiNativeAdRenderer(viewBinder));
  }

  @NonNull private ViewBinder getMoPubViewBinder() {
    return new ViewBinder.Builder(R.layout.search_ad).titleId(R.id.app_name)
        .mainImageId(R.id.native_main_image)
        .addExtra("primary_ad_view_layout", R.id.primary_ad_view_layout)
        .iconImageId(R.id.app_icon)
        .build();
  }

  private void setupToolbar() {
    String query = viewModel.getSearchQueryModel()
        .getFinalQuery();
    if (query.isEmpty() && !noResults) {
      toolbar.setTitle(R.string.search_hint_title);
      toolbar.setTitleMarginStart(100);
    } else if (query.isEmpty()) {
      toolbar.setTitle(R.string.search_hint_title);
    } else {
      toolbar.setTitle(query);
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

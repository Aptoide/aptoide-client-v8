package cm.aptoide.pt.view.search;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
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
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.util.HashMapNotNull;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.search.SearchAnalytics;
import cm.aptoide.pt.search.SearchBuilder;
import cm.aptoide.pt.search.SearchManager;
import cm.aptoide.pt.search.SearchNavigator;
import cm.aptoide.pt.store.StoreUtils;
import cm.aptoide.pt.view.fragment.FragmentView;
import cm.aptoide.pt.view.recycler.LinearLayoutManagerWithSmoothScroller;
import com.facebook.appevents.AppEventsLogger;
import com.jakewharton.rxbinding.view.RxView;
import java.util.List;
import okhttp3.OkHttpClient;
import org.parceler.Parcels;
import retrofit2.Converter;
import rx.Observable;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by neuro on 01-06-2016.
 */
public class SearchFragment extends FragmentView implements SearchView {

  private static final int LAYOUT = R.layout.global_search_fragment;
  private static final String VIEW_MODEL = "view_model";
  private SearchViewModel viewModel;
  private Button followedStoresButton;
  private Button allStoresButton;
  private LinearLayout buttonsLayout;
  private View noSearchLayout;
  private EditText noSearchLayoutSearchQuery;
  private ImageView noSearchLayoutSearchButton;
  private PublishSubject<Long> selectedElementSubject;
  private RecyclerView resultList;

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

  }

  @Override public void showAllStoresResult() {

  }

  @Override public Observable<Integer> selectedTab() {
    return Observable.merge(RxView.clicks(followedStoresButton)
        .map(__ -> 1), RxView.clicks(allStoresButton)
        .map(__ -> 2));
  }

  @Override public Observable<Long> selectedOneElementFromSearch() {
    return selectedElementSubject.asObservable();
  }

  @Override public Observable<Void> noSearchLayoutSearchButtonClick() {
    return RxView.clicks(noSearchLayoutSearchButton);
  }

  @Override public void showNoResultsImage() {
    noSearchLayout.setVisibility(View.VISIBLE);
    buttonsLayout.setVisibility(View.INVISIBLE);
    noSearchLayoutSearchButtonClick().subscribe(__ -> {
      String s = noSearchLayoutSearchQuery.getText()
          .toString();

      if (s.length() > 1) {
        getFragmentNavigator().navigateTo(AptoideApplication.getFragmentProvider()
            .newSearchFragment(s, viewModel.getStoreName()), true);
      }
    });
  }

  @Override public void setSubscribedSearchButtonHighlighted() {
    followedStoresButton.setBackgroundResource(R.drawable.search_button_background);
    followedStoresButton.setTextColor(getResources().getColor(R.color.white));
    allStoresButton.setTextColor(getResources().getColor(R.color.silver_dark));
    allStoresButton.setBackgroundResource(0);
  }

  @Override public void setEverywhereSearchButtonHighlighted() {
    allStoresButton.setBackgroundResource(R.drawable.search_button_background);
    allStoresButton.setTextColor(getResources().getColor(R.color.white));
    followedStoresButton.setTextColor(getResources().getColor(R.color.silver_dark));
    followedStoresButton.setBackgroundResource(0);
  }

  @Override public Observable<Void> clickFollowedStoresSearchButton() {
    return RxView.clicks(followedStoresButton);
  }

  @Override public Observable<Void> clickEverywhereSearchButton() {
    return RxView.clicks(allStoresButton);
  }

  @Override public void showLoading() {
    // TODO: 22/9/2017 sithengineer
    resultList.setVisibility(View.GONE);
  }

  @Override public void hideLoading() {
    // TODO: 22/9/2017 sithengineer
    resultList.setVisibility(View.VISIBLE);
  }

  @Override public Model getViewModel() {
    return viewModel;
  }

  @Override
  public void setupButtonVisibility(boolean hasSubscribedResults, boolean hasEverywhereResults) {
    if (viewModel.getStoreName() != null) {
      followedStoresButton.setText(viewModel.getStoreName());
      followedStoresButton.setVisibility(View.VISIBLE);
      return;
    }

    if (hasSubscribedResults) {
      followedStoresButton.setVisibility(View.VISIBLE);
    }
    if (hasEverywhereResults) {
      allStoresButton.setVisibility(View.VISIBLE);
    }
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    selectedElementSubject = PublishSubject.create();
    attachPresenter(createPresenter(), null);
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelable(VIEW_MODEL, Parcels.wrap(viewModel));
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu_search_results, menu);

    final SearchNavigator searchNavigator;
    if (viewModel.getStoreName() != null
        && viewModel.getStoreName()
        .length() > 0) {
      searchNavigator = new SearchNavigator(getFragmentNavigator(), viewModel.getStoreName());
    } else {
      searchNavigator = new SearchNavigator(getFragmentNavigator());
    }

    SearchBuilder searchBuilder =
        new SearchBuilder(menu.findItem(R.id.action_search), getActivity(), searchNavigator);
    searchBuilder.validateAndAttachSearch();
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
    final Scheduler ioScheduler = Schedulers.io();
    final Scheduler mainThreadScheduler = AndroidSchedulers.mainThread();

    final SearchResultAdapter adapter = new SearchResultAdapter();
    resultList.setAdapter(adapter);
    resultList.setLayoutManager(new LinearLayoutManagerWithSmoothScroller(getContext()));
    return new SearchPresenter(this, searchAnalytics, crashReport, mainThreadScheduler,
        searchManager, adapter);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    setHasOptionsMenu(true);

    /*
    @Override protected boolean displayHomeUpAsEnabled() {
      return true;
    }

    @Override public void setupToolbarDetails(Toolbar toolbar) {
      toolbar.setTitle(currentQuery);
    }
    */

    final View view = inflater.inflate(LAYOUT, container, false);
    bindViews(view);
    return view;
  }

  private void bindViews(View view) {
    resultList = (RecyclerView) view.findViewById(R.id.search_result_list);
    followedStoresButton = (Button) view.findViewById(R.id.subscribed);
    allStoresButton = (Button) view.findViewById(R.id.everywhere);
    buttonsLayout = (LinearLayout) view.findViewById(R.id.buttons_layout);
    noSearchLayout = view.findViewById(R.id.no_search_results_layout);
    noSearchLayoutSearchQuery = (EditText) view.findViewById(R.id.search_text);
    noSearchLayoutSearchButton = (ImageView) view.findViewById(R.id.ic_search_button);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    if (savedInstanceState != null) {
      viewModel = Parcels.unwrap(savedInstanceState.getParcelable(VIEW_MODEL));
    }

    if (viewModel.getSelectedButton() == 0) {
      setSubscribedSearchButtonHighlighted();
    } else {
      setEverywhereSearchButtonHighlighted();
    }
  }
}

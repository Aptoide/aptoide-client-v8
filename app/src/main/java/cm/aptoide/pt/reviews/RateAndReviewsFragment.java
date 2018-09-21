package cm.aptoide.pt.reviews;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.app.AppNavigator;
import cm.aptoide.pt.app.view.NewAppViewFragment;
import cm.aptoide.pt.comments.ListFullReviewsSuccessRequestListener;
import cm.aptoide.pt.comments.view.CommentDisplayable;
import cm.aptoide.pt.comments.view.CommentsReadMoreDisplayable;
import cm.aptoide.pt.comments.view.ItemCommentAdderView;
import cm.aptoide.pt.comments.view.RateAndReviewCommentDisplayable;
import cm.aptoide.pt.comments.view.ReviewsAdapter;
import cm.aptoide.pt.comments.view.SimpleReviewCommentAdder;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.AccessorFactory;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.Comment;
import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.dataprovider.model.v7.Review;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.GetAppRequest;
import cm.aptoide.pt.dataprovider.ws.v7.ListReviewsRequest;
import cm.aptoide.pt.install.InstalledRepository;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.navigator.ActivityResultNavigator;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.repository.RepositoryFactory;
import cm.aptoide.pt.store.StoreCredentialsProvider;
import cm.aptoide.pt.store.StoreCredentialsProviderImpl;
import cm.aptoide.pt.store.StoreTheme;
import cm.aptoide.pt.view.ThemeUtils;
import cm.aptoide.pt.view.dialog.DialogUtils;
import cm.aptoide.pt.view.fragment.AptoideBaseFragment;
import cm.aptoide.pt.view.recycler.EndlessRecyclerOnScrollListener;
import cm.aptoide.pt.view.recycler.displayable.Displayable;
import cm.aptoide.pt.view.recycler.displayable.ProgressBarDisplayable;
import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.android.FragmentEvent;
import java.util.List;
import javax.inject.Inject;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RateAndReviewsFragment extends AptoideBaseFragment<ReviewsAdapter>
    implements ItemCommentAdderView<Review, ReviewsAdapter> {

  private static final String TAG = RateAndReviewsFragment.class.getSimpleName();
  @Inject AppNavigator appNavigator;
  private SharedPreferences preferences;
  private DialogUtils dialogUtils;
  private long reviewId;
  private String storeName;
  private String appName;
  private long appId;
  private String packageName;
  private String storeTheme;
  private MenuItem installMenuItem;
  private EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;
  private StoreCredentialsProvider storeCredentialsProvider;
  private AptoideAccountManager accountManager;
  private BodyInterceptor<BaseBody> baseBodyInterceptor;
  private InstalledRepository installedRepository;
  private OkHttpClient httpClient;
  private Converter.Factory converterFactory;
  private TokenInvalidator tokenInvalidator;
  private ReviewsLanguageFilterDisplayable reviewsLanguageFilterDisplayable;

  public static RateAndReviewsFragment newInstance(long appId, String appName, String storeName,
      String packageName, String storeTheme) {
    RateAndReviewsFragment fragment = new RateAndReviewsFragment();
    Bundle args = new Bundle();
    args.putLong(BundleCons.APP_ID, appId);
    args.putString(BundleCons.APP_NAME, appName);
    args.putString(BundleCons.STORE_NAME, storeName);
    args.putString(BundleCons.PACKAGE_NAME, packageName);
    args.putString(BundleCons.STORE_THEME, storeTheme);
    fragment.setArguments(args);
    return fragment;
  }

  public static RateAndReviewsFragment newInstance(long appId, String appName, String storeName,
      String packageName, long reviewId) {
    RateAndReviewsFragment fragment = new RateAndReviewsFragment();
    Bundle args = new Bundle();
    args.putLong(BundleCons.APP_ID, appId);
    args.putString(BundleCons.APP_NAME, appName);
    args.putString(BundleCons.STORE_NAME, storeName);
    args.putString(BundleCons.PACKAGE_NAME, packageName);
    args.putLong(BundleCons.REVIEW_ID, reviewId);
    fragment.setArguments(args);
    return fragment;
  }

  public long getReviewId() {
    return reviewId;
  }

  public String getStoreName() {
    return storeName;
  }

  public String getAppName() {
    return appName;
  }

  @Override protected boolean displayHomeUpAsEnabled() {
    return true;
  }

  @Override public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu_install, menu);
    installMenuItem = menu.findItem(R.id.menu_install);

    installedRepository.getInstalled(packageName)
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(installed -> {
          if (installed != null) {
            // app installed... update text
            installMenuItem.setTitle(R.string.reviewappview_button_open);
          }
        }, err -> {
          CrashReport.getInstance()
              .log(err);
        });
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int itemId = item.getItemId();
    if (itemId == android.R.id.home) {
      getActivity().onBackPressed();
      return true;
    }
    if (itemId == R.id.menu_install) {
      appNavigator.navigateWithPackageAndStoreNames(packageName, storeName,
          NewAppViewFragment.OpenType.OPEN_AND_INSTALL);
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override public void loadExtras(Bundle args) {
    super.loadExtras(args);
    appId = args.getLong(BundleCons.APP_ID);
    reviewId = args.getLong(BundleCons.REVIEW_ID);
    packageName = args.getString(BundleCons.PACKAGE_NAME);
    storeName = args.getString(BundleCons.STORE_NAME);
    appName = args.getString(BundleCons.APP_NAME);
    storeTheme = args.getString(BundleCons.STORE_THEME);
  }

  @Override public int getContentViewId() {
    return R.layout.fragment_rate_and_reviews;
  }

  @Override public void bindViews(View view) {
    super.bindViews(view);
    final FloatingActionButton floatingActionButton =
        (FloatingActionButton) view.findViewById(R.id.fab);

    RxView.clicks(floatingActionButton)
        .flatMap(__ -> dialogUtils.showRateDialog(getActivity(), appName, packageName, storeName))
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(__ -> {
        }, err -> CrashReport.getInstance()
            .log(err));
  }

  @Override public void onDestroyView() {
    ThemeUtils.setStatusBarThemeColor(getActivity(), StoreTheme.get(getDefaultTheme()));
    ThemeUtils.setStoreTheme(getActivity(), getDefaultTheme());
    super.onDestroyView();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    if (storeTheme != null) {
      ThemeUtils.setStatusBarThemeColor(getActivity(), StoreTheme.get(storeTheme));
      ThemeUtils.setStoreTheme(getActivity(), storeTheme);
    }
  }

  @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    super.load(create, refresh, savedInstanceState);
    Logger.getInstance()
        .d(TAG, "Other versions should refresh? " + create);
    fetchRating(refresh);
  }

  @Override public void onViewCreated() {
    super.onViewCreated();
    dialogUtils = new DialogUtils(accountManager,
        ((ActivityResultNavigator) getContext()).getAccountNavigator(), baseBodyInterceptor,
        httpClient, converterFactory, installedRepository, tokenInvalidator,
        ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences(),
        getContext().getResources());
  }

  private void fetchRating(boolean refresh) {
    GetAppRequest.of(packageName, baseBodyInterceptor, appId, httpClient, converterFactory,
        tokenInvalidator,
        ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences())
        .observe(refresh, ManagerPreferences.getAndResetForceServerRefresh(preferences))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(getApp -> {
          if (getApp.isOk()) {
            GetAppMeta.App data = getApp.getNodes()
                .getMeta()
                .getData();
            setupTitle(data.getName());
            addDisplayable(0, new ReviewsRatingDisplayable(data), true);
            addDisplayable(reviewsLanguageFilterDisplayable =
                new ReviewsLanguageFilterDisplayable(languageFilter -> {
                  removeDisplayables(1, getDisplayablesSize() - 1);
                  fetchReviews(languageFilter);
                }));
          }
          finishLoading();
        }, err -> {
          CrashReport.getInstance()
              .log(err);
        });
  }

  void fetchReviews(LanguageFilterHelper.LanguageFilter languageFilter) {
    addDisplayable(reviewsLanguageFilterDisplayable);
    ListReviewsRequest reviewsRequest = createListReviewsRequest(languageFilter.getValue());

    getRecyclerView().removeOnScrollListener(endlessRecyclerOnScrollListener);
    endlessRecyclerOnScrollListener =
        new EndlessRecyclerOnScrollListener(this.getAdapter(), reviewsRequest,
            new ListFullReviewsSuccessRequestListener(this, new StoreCredentialsProviderImpl(
                AccessorFactory.getAccessorFor(
                    ((AptoideApplication) getContext().getApplicationContext()
                        .getApplicationContext()).getDatabase(), Store.class)), baseBodyInterceptor,
                httpClient, converterFactory, tokenInvalidator,
                ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences(),
                getFragmentNavigator(),
                ((AptoideApplication) getContext().getApplicationContext()).getFragmentProvider()),
            (throwable) -> throwable.printStackTrace());

    endlessRecyclerOnScrollListener.addOnEndlessFinishListener(endlessRecyclerOnScrollListener1 -> {
      if (languageFilter.hasMoreCountryCodes()) {
        endlessRecyclerOnScrollListener.reset(createListReviewsRequest(languageFilter.inc()
            .getValue()));
      }
    });

    getRecyclerView().addOnScrollListener(endlessRecyclerOnScrollListener);
    endlessRecyclerOnScrollListener.onLoadMore(false, false);
  }

  private ListReviewsRequest createListReviewsRequest(String languagesFilterSort) {
    return ListReviewsRequest.of(storeName, packageName, storeCredentialsProvider.get(storeName),
        baseBodyInterceptor, httpClient, converterFactory, tokenInvalidator,
        ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences(),
        languagesFilterSort);
  }

  public void setupTitle(String title) {
    if (hasToolbar()) {
      getToolbar().setTitle(title);
    }
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
    preferences =
        ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences();
    tokenInvalidator =
        ((AptoideApplication) getContext().getApplicationContext()).getTokenInvalidator();
    accountManager =
        ((AptoideApplication) getContext().getApplicationContext()).getAccountManager();
    installedRepository =
        RepositoryFactory.getInstalledRepository(getContext().getApplicationContext());
    baseBodyInterceptor =
        ((AptoideApplication) getContext().getApplicationContext()).getAccountSettingsBodyInterceptorPoolV7();
    storeCredentialsProvider = new StoreCredentialsProviderImpl(AccessorFactory.getAccessorFor(
        ((AptoideApplication) getContext().getApplicationContext()
            .getApplicationContext()).getDatabase(), Store.class));
    installedRepository =
        RepositoryFactory.getInstalledRepository(getContext().getApplicationContext());
    httpClient = ((AptoideApplication) getContext().getApplicationContext()).getDefaultClient();
    converterFactory = WebService.getDefaultConverter();
    setHasOptionsMenu(true);
  }

  @NonNull @Override
  public CommentsReadMoreDisplayable createReadMoreDisplayable(final int itemPosition,
      Review review) {
    return new CommentsReadMoreDisplayable(review.getId(), true, review.getCommentList()
        .getDataList()
        .getNext(), new SimpleReviewCommentAdder(itemPosition, this));
  }

  @Override protected ReviewsAdapter createAdapter() {
    return new ReviewsAdapter<>(RateAndReviewCommentDisplayable.class);
  }

  @Override
  public void createDisplayableComments(List<Comment> comments, List<Displayable> displayables) {
    for (final Comment comment : comments) {
      displayables.add(new CommentDisplayable(comment, getFragmentNavigator(),
          ((AptoideApplication) getContext().getApplicationContext()).getFragmentProvider()));
    }
  }

  public void checkAndRemoveProgressBarDisplayable() {
    for (int i = 0; i < getAdapter().getItemCount(); i++) {
      Displayable displayable = getAdapter().getDisplayable(i);
      if (displayable instanceof ProgressBarDisplayable) {
        getAdapter().removeDisplayable(i);
        getAdapter().notifyItemRemoved(i);
      }
    }
  }

  /**
   * Bundle of constants
   */
  public static class BundleCons {
    public static final String APP_ID = "app_id";
    public static final String PACKAGE_NAME = "package_name";
    public static final String STORE_NAME = "store_name";
    public static final String APP_NAME = "app_name";
    public static final String REVIEW_ID = "review_id";
    public static final String STORE_THEME = "store_theme";
  }
}

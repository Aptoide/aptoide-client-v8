package cm.aptoide.pt.viewRateAndCommentReviews;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.InstalledAccessor;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.model.v7.Comment;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.model.v7.Review;
import cm.aptoide.pt.util.schedulers.ConcreteSchedulerProvider;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.adapters.CommentsAdapter;
import cm.aptoide.pt.v8engine.fragment.AptoideBaseFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.AppViewFragment;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.util.DialogUtils;
import cm.aptoide.pt.v8engine.util.StoreThemeEnum;
import cm.aptoide.pt.v8engine.util.ThemeUtils;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.CommentDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.listeners.RxEndlessRecyclerView;
import cm.aptoide.pt.viewRateAndCommentReviews.layout.RatingBarsLayout;
import cm.aptoide.pt.viewRateAndCommentReviews.layout.RatingTotalsLayout;
import com.jakewharton.rxbinding.view.RxView;
import java.util.List;
import lombok.Getter;
import rx.Observable;

public class RateAndReviewsFragmentNew extends AptoideBaseFragment<CommentsAdapter>
    implements ItemCommentAdderView<Review, CommentsAdapter>, RateAndReviewsView {

  private static final String TAG = RateAndReviewsFragmentNew.class.getSimpleName();
  private static final String APP_ID = "app_id";
  private static final String PACKAGE_NAME = "package_name";
  private static final String STORE_NAME = "store_name";
  private static final String APP_NAME = "app_name";
  private static final String REVIEW_ID = "review_id";
  private static final String STORE_THEME = "store_theme";

  // view data
  private long appId;
  @Getter private long reviewId;
  private String packageName;
  @Getter private String storeName;
  private String storeTheme;
  @Getter private String appName;
  private MenuItem installMenuItem;
  // views
  private RatingTotalsLayout ratingTotalsLayout;
  private RatingBarsLayout ratingBarsLayout;
  private FloatingActionButton floatingActionButton;
  private DialogUtils dialogUtils;

  //
  // static constructors
  //

  public static RateAndReviewsFragmentNew newInstance(long appId, String appName, String storeName,
      String packageName, String storeTheme) {
    RateAndReviewsFragmentNew fragment = new RateAndReviewsFragmentNew();
    Bundle args = new Bundle();
    args.putLong(APP_ID, appId);
    args.putString(APP_NAME, appName);
    args.putString(STORE_NAME, storeName);
    args.putString(PACKAGE_NAME, packageName);
    args.putString(STORE_THEME, storeTheme);
    fragment.setArguments(args);
    return fragment;
  }

  public static RateAndReviewsFragmentNew newInstance(long appId, String appName, String storeName,
      String packageName, long reviewId) {
    RateAndReviewsFragmentNew fragment = new RateAndReviewsFragmentNew();
    Bundle args = new Bundle();
    args.putLong(APP_ID, appId);
    args.putString(APP_NAME, appName);
    args.putString(STORE_NAME, storeName);
    args.putString(PACKAGE_NAME, packageName);
    args.putLong(REVIEW_ID, reviewId);
    fragment.setArguments(args);
    return fragment;
  }

  //
  // base methods
  //

  @Override protected boolean displayHomeUpAsEnabled() {
    return true;
  }

  @Override @NonNull
  public CommentsReadMoreDisplayable createReadMoreDisplayable(final int itemPosition,
      Review review) {
    return new CommentsReadMoreDisplayable(review.getId(), true,
        review.getCommentList().getDatalist().getNext(),
        new SimpleReviewCommentAdder(itemPosition, this));
  }

  @Override
  public void createDisplayableComments(List<Comment> comments, List<Displayable> displayables) {
    for (final Comment comment : comments) {
      displayables.add(new CommentDisplayable(comment));
    }
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    dialogUtils = new DialogUtils();
    final RateAndReviewsPresenter presenter =
        new RateAndReviewsPresenter(appId, storeName, packageName, this,
            ConcreteSchedulerProvider.getInstance());

    attachPresenter(presenter, savedInstanceState);
  }

  @Override public void loadExtras(Bundle args) {
    super.loadExtras(args);
    appId = args.getLong(APP_ID);
    reviewId = args.getLong(REVIEW_ID);
    packageName = args.getString(PACKAGE_NAME);
    storeName = args.getString(STORE_NAME);
    appName = args.getString(APP_NAME);
    storeTheme = args.getString(STORE_THEME);
  }

  @Override public int getContentViewId() {
    return R.layout.fragment_rate_and_reviews;
  }

  //
  // MVP methods
  //

  @CallSuper @Override public void bindViews(View view) {
    super.bindViews(view);
    floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab);
    setHasOptionsMenu(true);

    ratingTotalsLayout = new RatingTotalsLayout(view);
    ratingBarsLayout = new RatingBarsLayout(view);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    if (storeTheme != null) {
      ThemeUtils.setStatusBarThemeColor(getActivity(), StoreThemeEnum.get(storeTheme));
      ThemeUtils.setStoreTheme(getActivity(), storeTheme);
    }
  }

  @CallSuper @Override
  public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    super.load(create, refresh, savedInstanceState);
    // ??
  }

  @Override public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu_install, menu);
    installMenuItem = menu.findItem(R.id.menu_install);

    InstalledAccessor accessor = AccessorFactory.getAccessorFor(Installed.class);
    accessor.get(packageName).subscribe(installed -> {
      if (installed != null) {
        // app installed... update text
        installMenuItem.setTitle(R.string.open);
      }
    }, err -> {
      CrashReport.getInstance().log(err);
    });
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int itemId = item.getItemId();
    if (itemId == android.R.id.home) {
      getActivity().onBackPressed();
      return true;
    }
    if (itemId == R.id.menu_install) {
      // todo Navigator n = new Navigator();
      ((FragmentShower) getContext()).pushFragmentV4(V8Engine.getFragmentProvider()
          .newAppViewFragment(packageName, storeName, AppViewFragment.OpenType.OPEN_AND_INSTALL));
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override protected CommentsAdapter createAdapter() {
    return new CommentsAdapter<>(RateAndReviewCommentDisplayable.class);
  }

  @Override public Observable<Integer> nextReviews() {
    return RxEndlessRecyclerView.loadMore(getRecyclerView(), getAdapter());
  }

  @Override public Observable<Void> rateApp() {
    return RxView.clicks(floatingActionButton);
  }

  @Override public Observable<GenericDialogs.EResponse> showRateView() {
    return dialogUtils.showRateDialog(getActivity(), appName, packageName, storeName);
  }

  @Override public void showNextReviews(int offset, List<Review> reviews) {

  }

  @Override public void showRating(GetAppMeta.Stats.Rating rating) {

  }


  @Override public void showError(Throwable err) {

  }


}

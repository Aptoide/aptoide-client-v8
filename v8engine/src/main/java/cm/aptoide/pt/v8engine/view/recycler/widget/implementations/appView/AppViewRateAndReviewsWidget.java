/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 29/08/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.ListReviewsRequest;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.model.v7.Review;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.account.AccountNavigator;
import cm.aptoide.pt.v8engine.util.DialogUtils;
import cm.aptoide.pt.v8engine.util.LinearLayoutManagerWithSmoothScroller;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewRateAndCommentsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.Target;
import com.jakewharton.rxbinding.view.RxView;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by sithengineer on 30/06/16.
 */
@Displayables({ AppViewRateAndCommentsDisplayable.class }) public class AppViewRateAndReviewsWidget
    extends Widget<AppViewRateAndCommentsDisplayable> {

  public static final long TIME_BETWEEN_SCROLL = 2 * DateUtils.SECOND_IN_MILLIS;
  private static final String TAG = AppViewRateAndReviewsWidget.class.getSimpleName();
  private static final int MAX_COMMENTS = 3;
  private DialogUtils dialogUtils;
  private AptoideAccountManager accountManager;
  private View emptyReviewsLayout;
  private View ratingLayout;
  private View commentsLayout;

  private TextView usersVotedTextView;
  private TextView ratingValue;
  private RatingBar ratingBar;

  private Button rateThisButton;
  private Button rateThisButtonLarge;
  private Button readAllButton;

  private RecyclerView topReviewsList;
  private ContentLoadingProgressBar topReviewsProgress;

  private String appName;
  private String packageName;
  private String storeName;
  private int usersToVote;
  private TextView emptyReviewTextView;
  private BodyInterceptor<BaseBody> bodyInterceptor;

  public AppViewRateAndReviewsWidget(@NonNull View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    emptyReviewsLayout = itemView.findViewById(R.id.empty_reviews_layout);
    ratingLayout = itemView.findViewById(R.id.rating_layout);
    commentsLayout = itemView.findViewById(R.id.comments_layout);

    usersVotedTextView = (TextView) itemView.findViewById(R.id.users_voted);
    emptyReviewTextView = (TextView) itemView.findViewById(R.id.empty_review_text);
    ratingValue = (TextView) itemView.findViewById(R.id.rating_value);
    ratingBar = (RatingBar) itemView.findViewById(R.id.rating_bar);
    rateThisButton = (Button) itemView.findViewById(R.id.rate_this_button);
    rateThisButtonLarge = (Button) itemView.findViewById(R.id.rate_this_button2);
    readAllButton = (Button) itemView.findViewById(R.id.read_all_button);

    topReviewsList = (RecyclerView) itemView.findViewById(R.id.top_comments_list);
    topReviewsProgress =
        (ContentLoadingProgressBar) itemView.findViewById(R.id.top_comments_progress);
  }

  @Override public void bindView(AppViewRateAndCommentsDisplayable displayable) {
    GetApp pojo = displayable.getPojo();
    GetAppMeta.App app = pojo.getNodes().getMeta().getData();
    GetAppMeta.Stats stats = app.getStats();

    accountManager = ((V8Engine) getContext().getApplicationContext()).getAccountManager();
    bodyInterceptor = ((V8Engine) getContext().getApplicationContext()).getBaseBodyInterceptor();
    dialogUtils = new DialogUtils(accountManager,
        new AccountNavigator(getFragmentNavigator(), accountManager, getActivityNavigator()),
        bodyInterceptor);
    appName = app.getName();
    packageName = app.getPackageName();
    storeName = app.getStore().getName();

    usersToVote = stats.getRating().getTotal();
    usersVotedTextView.setText(AptoideUtils.StringU.withSuffix(usersToVote));

    float ratingAvg = stats.getRating().getAvg();
    ratingValue.setText(String.format(AptoideUtils.LocaleU.DEFAULT, "%.1f", ratingAvg));
    ratingBar.setRating(ratingAvg);

    Action1<Throwable> handleError = throwable -> CrashReport.getInstance().log(throwable);

    final FragmentActivity context = getContext();
    Observable<GenericDialogs.EResponse> showRateDialog =
        dialogUtils.showRateDialog(context, appName, packageName, storeName);

    compositeSubscription.add(
        RxView.clicks(rateThisButton).flatMap(__ -> showRateDialog).subscribe(__ -> {
        }, handleError));
    compositeSubscription.add(
        RxView.clicks(rateThisButtonLarge).flatMap(__ -> showRateDialog).subscribe(__ -> {
        }, handleError));
    compositeSubscription.add(
        RxView.clicks(ratingLayout).flatMap(__ -> showRateDialog).subscribe(__ -> {
        }, handleError));

    Action1<Void> commentsOnClickListener = __ -> {
      Fragment fragment = V8Engine.getFragmentProvider()
          .newRateAndReviewsFragment(app.getId(), app.getName(), app.getStore().getName(),
              app.getPackageName(), app.getStore().getAppearance().getTheme());
      getFragmentNavigator().navigateTo(fragment);
    };
    compositeSubscription.add(
        RxView.clicks(readAllButton).subscribe(commentsOnClickListener, handleError));
    compositeSubscription.add(
        RxView.clicks(commentsLayout).subscribe(commentsOnClickListener, handleError));

    LinearLayoutManagerWithSmoothScroller layoutManager =
        new LinearLayoutManagerWithSmoothScroller(context, LinearLayoutManager.HORIZONTAL, false);
    topReviewsList.setLayoutManager(layoutManager);
    // because otherwise the AppBar won't be collapsed
    topReviewsList.setNestedScrollingEnabled(false);

    loadReviews(displayable.getStoreCredentials());
  }

  private void loadReviews(BaseRequestWithStore.StoreCredentials storeCredentials) {
    loadTopReviews(storeName, packageName, storeCredentials);
  }

  private void loadTopReviews(String storeName, String packageName,
      BaseRequestWithStore.StoreCredentials storeCredentials) {
    Subscription subscription =
        ListReviewsRequest.ofTopReviews(storeName, packageName, MAX_COMMENTS, storeCredentials,
            bodyInterceptor)
            .observe(true)
            .observeOn(AndroidSchedulers.mainThread())
            .map(listReviews -> {
              List<Review> reviews = listReviews.getDatalist().getList();
              if (reviews == null || reviews.isEmpty()) {
                loadedData(false);
                return new TopReviewsAdapter();
              }

              loadedData(true);
              final List<Review> list = listReviews.getDatalist().getList();
              return new TopReviewsAdapter(list.toArray(new Review[list.size()]));
            })
            .doOnNext(topReviewsAdapter -> topReviewsList.setAdapter(topReviewsAdapter))
            .flatMap(topReviewsAdapter -> scheduleAnimations(topReviewsAdapter.getItemCount()))
            .subscribe(topReviewsAdapter -> {
              // does nothing
            }, err -> {
              loadedData(false);
              topReviewsList.setAdapter(new TopReviewsAdapter());
              CrashReport.getInstance().log(err);
            });
    compositeSubscription.add(subscription);
  }

  private void loadedData(boolean hasReviews) {

    topReviewsProgress.setVisibility(View.GONE);

    if (hasReviews) {
      ratingLayout.setVisibility(View.VISIBLE);
      emptyReviewsLayout.setVisibility(View.GONE);
      commentsLayout.setVisibility(View.VISIBLE);
      rateThisButtonLarge.setVisibility(View.GONE);
      rateThisButton.setVisibility(View.VISIBLE);
    } else {
      ratingLayout.setVisibility(View.VISIBLE);
      emptyReviewsLayout.setVisibility(View.VISIBLE);
      commentsLayout.setVisibility(View.GONE);
      rateThisButtonLarge.setVisibility(View.VISIBLE);
      rateThisButton.setVisibility(View.INVISIBLE);

      if (usersToVote == 0) {
        emptyReviewTextView.setText(R.string.be_the_first_to_rate_this_app);
      }
    }
  }

  private Observable<Integer> scheduleAnimations(int topReviewsCount) {
    if (topReviewsCount <= 1) {
      // not enough elements for animation
      Logger.w(TAG, "Not enough top reviews to do paging animation.");
      return Observable.empty();
    }

    return Observable.range(0, topReviewsCount)
        .concatMap(pos -> Observable.just(pos)
            .delay(TIME_BETWEEN_SCROLL, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext(pos2 -> topReviewsList.smoothScrollToPosition(pos2)));
  }

  private static final class TopReviewsAdapter
      extends RecyclerView.Adapter<MiniTopReviewViewHolder> {

    private final Review[] reviews;

    public TopReviewsAdapter() {
      this(null);
    }

    public TopReviewsAdapter(Review[] reviews) {
      this.reviews = reviews;
    }

    @Override public MiniTopReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      LayoutInflater inflater = LayoutInflater.from(parent.getContext());
      return new MiniTopReviewViewHolder(
          inflater.inflate(MiniTopReviewViewHolder.LAYOUT_ID, parent, false));
    }

    @Override public void onBindViewHolder(MiniTopReviewViewHolder holder, int position) {
      holder.setup(reviews[position]);
    }

    @Override public int getItemCount() {
      return reviews == null ? 0 : reviews.length;
    }

    @Override public void onViewRecycled(MiniTopReviewViewHolder holder) {
      holder.cancelImageLoad();
      super.onViewRecycled(holder);
    }
  }

  private static final class MiniTopReviewViewHolder extends RecyclerView.ViewHolder {

    private static final int LAYOUT_ID = R.layout.mini_top_comment;

    private static final AptoideUtils.DateTimeU DATE_TIME_U = AptoideUtils.DateTimeU.getInstance();

    private ImageView userIconImageView;
    private RatingBar ratingBar;
    private TextView commentTitle;
    private TextView userName;
    private TextView addedDate;
    private TextView commentText;
    private Target<GlideDrawable> imageLoadingTarget;

    private MiniTopReviewViewHolder(View itemView) {
      super(itemView);
      bindViews(itemView);
    }

    private void bindViews(View view) {
      userIconImageView = (ImageView) view.findViewById(R.id.user_icon);
      ratingBar = (RatingBar) view.findViewById(R.id.rating_bar);
      commentTitle = (TextView) view.findViewById(R.id.comment_title);
      userName = (TextView) view.findViewById(R.id.user_name);
      addedDate = (TextView) view.findViewById(R.id.added_date);
      commentText = (TextView) view.findViewById(R.id.comment);
    }

    public void setup(Review review) {
      String imageUrl = review.getUser().getAvatar();
      Context context = itemView.getContext();
      //Context context = itemView.getContext().getApplicationContext();
      imageLoadingTarget = ImageLoader.with(context)
          .loadWithCircleTransformAndPlaceHolderAvatarSize(imageUrl, userIconImageView,
              R.drawable.layer_1);
      userName.setText(review.getUser().getName());
      ratingBar.setRating(review.getStats().getRating());
      commentTitle.setText(review.getTitle());
      commentText.setText(review.getBody());
      addedDate.setText(DATE_TIME_U.getTimeDiffString(review.getAdded().getTime()));
    }

    public void cancelImageLoad() {
      if (imageLoadingTarget != null) {
        ImageLoader.cancel(imageLoadingTarget);
      }
    }
  }
}

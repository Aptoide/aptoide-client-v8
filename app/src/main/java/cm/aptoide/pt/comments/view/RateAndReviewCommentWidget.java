/*
 * Copyright (c) 2016.
 * Modified on 09/08/2016.
 */

package cm.aptoide.pt.comments.view;

import android.content.res.Resources;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatRatingBar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.comments.CommentAdder;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.model.v7.Comment;
import cm.aptoide.pt.dataprovider.model.v7.Review;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.ListCommentsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.SetReviewRatingRequest;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.navigator.ActivityResultNavigator;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.android.FragmentEvent;
import java.util.List;
import java.util.Locale;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

public class RateAndReviewCommentWidget extends Widget<RateAndReviewCommentDisplayable> {

  public static final int FULL_COMMENTS_LIMIT = 3;
  private static final String TAG = RateAndReviewCommentWidget.class.getSimpleName();
  private static final Locale LOCALE = Locale.getDefault();
  private static final int DEFAULT_LIMIT = 3;
  private TextView reply;
  private TextView showHideReplies;
  private TextView flagHelfull;
  private TextView flagNotHelfull;

  private AppCompatRatingBar ratingBar;
  private TextView reviewTitle;
  private TextView reviewDate;
  private TextView reviewText;

  private ImageView userImage;
  private TextView username;

  private boolean isCommentsCollapsed = false;
  private View notHelpfullButtonLayout;
  private View helpfullButtonLayout;
  private AptoideAccountManager accountManager;
  private AccountNavigator accountNavigator;
  private BodyInterceptor<BaseBody> bodyInterceptor;
  private OkHttpClient httpClient;
  private Converter.Factory converterFactory;
  private TokenInvalidator tokenInvalidator;

  public RateAndReviewCommentWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    reply = (TextView) itemView.findViewById(R.id.write_reply_btn);
    showHideReplies = (TextView) itemView.findViewById(R.id.show_replies_btn);
    flagHelfull = (TextView) itemView.findViewById(R.id.helpful_btn);
    flagNotHelfull = (TextView) itemView.findViewById(R.id.not_helpful_btn);

    ratingBar = (AppCompatRatingBar) itemView.findViewById(R.id.rating_bar);
    reviewTitle = (TextView) itemView.findViewById(R.id.comment_title);
    reviewDate = (TextView) itemView.findViewById(R.id.added_date);
    reviewText = (TextView) itemView.findViewById(R.id.comment);

    userImage = (ImageView) itemView.findViewById(R.id.user_icon);
    username = (TextView) itemView.findViewById(R.id.user_name);

    helpfullButtonLayout = itemView.findViewById(R.id.helpful_layout);
    notHelpfullButtonLayout = itemView.findViewById(R.id.not_helpful_layout);
  }

  @Override public void bindView(RateAndReviewCommentDisplayable displayable) {
    final Review review = displayable.getPojo()
        .getReview();
    final String appName = displayable.getPojo()
        .getAppName();

    tokenInvalidator =
        ((AptoideApplication) getContext().getApplicationContext()).getTokenInvalidator();
    httpClient = ((AptoideApplication) getContext().getApplicationContext()).getDefaultClient();
    converterFactory = WebService.getDefaultConverter();

    accountManager =
        ((AptoideApplication) getContext().getApplicationContext()).getAccountManager();
    bodyInterceptor =
        ((AptoideApplication) getContext().getApplicationContext()).getAccountSettingsBodyInterceptorPoolV7();
    accountNavigator = ((ActivityResultNavigator) getContext()).getAccountNavigator();
    final FragmentActivity context = getContext();
    ImageLoader.with(context)
        .loadWithCircleTransformAndPlaceHolderAvatarSize(review.getUser()
            .getAvatar(), userImage, R.drawable.layer_1);
    username.setText(review.getUser()
        .getName());
    ratingBar.setRating(review.getStats()
        .getRating());
    reviewTitle.setText(review.getTitle());
    reviewText.setText(review.getBody());
    reviewDate.setText(AptoideUtils.DateTimeU.getInstance(getContext())
        .getTimeDiffString(context, review.getAdded()
            .getTime(), getContext().getResources()));

    if (DisplayMetrics.DENSITY_300 > context.getResources()
        .getDisplayMetrics().densityDpi) {
      flagHelfull.setText("");
      flagNotHelfull.setText("");
    }

    final CommentAdder commentAdder = displayable.getCommentAdder();
    final long reviewId = review.getId();

    compositeSubscription.add(RxView.clicks(reply)
        .flatMap(a -> {
          if (accountManager.isLoggedIn()) {
            FragmentManager fm = context.getSupportFragmentManager();
            CommentDialogFragment commentDialogFragment =
                CommentDialogFragment.newInstanceReview(review.getId(), appName);
            commentDialogFragment.show(fm, "fragment_comment_dialog");

            return commentDialogFragment.lifecycle()
                .filter(event -> event.equals(FragmentEvent.DESTROY_VIEW))
                .doOnNext(b -> {
                  ManagerPreferences.setForceServerRefreshFlag(true,
                      ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences());
                  commentAdder.collapseComments();
                  loadCommentsForThisReview(reviewId, FULL_COMMENTS_LIMIT, commentAdder);
                })
                .flatMap(event -> Observable.empty());
          } else {
            return ShowMessage.asObservableSnack(ratingBar, R.string.you_need_to_be_logged_in,
                R.string.login, snackView -> {
                  accountNavigator.navigateToAccountView(
                      Analytics.Account.AccountOrigins.REPLY_REVIEW);
                })
                .toObservable();
          }
        })
        .subscribe(a -> { /* do nothing */ }, err -> {
          CrashReport.getInstance()
              .log(err);
        }));

    compositeSubscription.add(RxView.clicks(helpfullButtonLayout)
        .subscribe(a -> {
          setReviewRating(review.getId(), true);
        }));

    compositeSubscription.add(RxView.clicks(notHelpfullButtonLayout)
        .subscribe(a -> {
          setReviewRating(review.getId(), false);
        }));

    compositeSubscription.add(RxView.clicks(showHideReplies)
        .subscribe(a -> {
          if (isCommentsCollapsed) {
            loadCommentsForThisReview(review.getId(), FULL_COMMENTS_LIMIT,
                displayable.getCommentAdder());
            showHideReplies.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_up_arrow,
                0);
            isCommentsCollapsed = false;
          } else {
            displayable.getCommentAdder()
                .collapseComments();
            showHideReplies.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_down_arrow,
                0);
            isCommentsCollapsed = true;
          }
        }));

    final Resources.Theme theme = context.getTheme();
    final Resources res = context.getResources();
    int color =
        getItemId() % 2 == 0 ? R.color.white : R.color.displayable_rate_and_review_background;

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      itemView.setBackgroundColor(res.getColor(color, theme));
    } else {
      itemView.setBackgroundColor(res.getColor(color));
    }
    int numberComments = displayable.getNumberComments();
    if (numberComments > 0) {
      showHideReplies.setVisibility(View.VISIBLE);
      showHideReplies.setText(
          AptoideUtils.StringU.getFormattedString(R.string.reviews_expand_button,
              getContext().getResources(), numberComments));
    } else {
      showHideReplies.setVisibility(View.GONE);
    }

    compositeSubscription.add(RxView.clicks(itemView)
        .doOnNext(click -> displayable.itemClicked())
        .subscribe(__ -> {
        }, throwable -> CrashReport.getInstance()
            .log(throwable)));
  }

  private void loadCommentsForThisReview(long reviewId, int limit, CommentAdder commentAdder) {
    ListCommentsRequest.of(reviewId, limit, true, bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator,
        ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences())
        .execute(listComments -> {
          if (listComments.isOk()) {
            List<Comment> comments = listComments.getDataList()
                .getList();
            commentAdder.addComment(comments);
          } else {
            Logger.e(TAG, "error loading comments");
            ShowMessage.asSnack(flagHelfull, R.string.unknown_error);
          }
        }, err -> {
          Logger.e(TAG, err);
          ShowMessage.asSnack(flagHelfull, R.string.unknown_error);
        }, true);
  }

  private void setReviewRating(long reviewId, boolean positive) {
    setHelpButtonsClickable(false);

    if (accountManager.isLoggedIn()) {
      SetReviewRatingRequest.of(reviewId, positive, bodyInterceptor, httpClient, converterFactory,
          tokenInvalidator,
          ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences())
          .execute(response -> {
            if (response == null) {
              Logger.e(TAG, "empty response");
              return;
            }

            if (response.getError() != null) {
              Logger.e(TAG, response.getError()
                  .getDescription());
              return;
            }

            List<BaseV7Response.Error> errorList = response.getErrors();
            if (errorList != null && !errorList.isEmpty()) {
              for (final BaseV7Response.Error error : errorList) {
                Logger.e(TAG, error.getDescription());
              }
              return;
            }

            // success
            Logger.d(TAG, String.format("review %d was marked as %s", reviewId,
                positive ? "positive" : "negative"));
            setHelpButtonsClickable(true);
            ShowMessage.asSnack(flagHelfull, R.string.thank_you_for_your_opinion);
          }, err -> {
            ShowMessage.asSnack(flagHelfull, R.string.unknown_error);
            Logger.e(TAG, err);
            setHelpButtonsClickable(true);
          }, true);
    } else {
      ShowMessage.asSnack(getContext(), R.string.you_need_to_be_logged_in, R.string.login,
          snackView -> {
            accountNavigator.navigateToAccountView(
                Analytics.Account.AccountOrigins.REVIEW_FEEDBACK);
          }, Snackbar.LENGTH_SHORT);
      setHelpButtonsClickable(true);
    }
  }

  private void setHelpButtonsClickable(boolean clickable) {
    flagHelfull.setClickable(clickable);
    flagNotHelfull.setClickable(clickable);
    notHelpfullButtonLayout.setClickable(clickable);
    helpfullButtonLayout.setClickable(clickable);
  }
}

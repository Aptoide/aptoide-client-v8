/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 09/08/2016.
 */

package cm.aptoide.pt.viewRateAndCommentReviews;

import android.content.res.Resources;
import android.os.Build;
import android.support.v7.widget.AppCompatRatingBar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.ws.v7.ListCommentsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.PostCommentForReviewRequest;
import cm.aptoide.pt.dataprovider.ws.v7.SetReviewRatingRequest;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.model.v7.Comment;
import cm.aptoide.pt.model.v7.Review;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import java.util.List;
import java.util.Locale;
import rx.Observable;

@Displayables({ RateAndReviewCommentDisplayable.class }) public class RateAndReviewCommentWidget
    extends Widget<RateAndReviewCommentDisplayable> {

  public static final int FULL_COMMENTS_LIMIT = 3;
  private static final String TAG = RateAndReviewCommentWidget.class.getSimpleName();
  private static final AptoideUtils.DateTimeU DATE_TIME_U = AptoideUtils.DateTimeU.getInstance();
  private static final Locale LOCALE = Locale.getDefault();
  private static final int DEFAULT_LIMIT = 3;
  private TextView reply;
  private TextView showHideReplies;
  private Button flagHelfull;
  private Button flagNotHelfull;

  private AppCompatRatingBar ratingBar;
  private TextView reviewTitle;
  private TextView reviewDate;
  private TextView reviewText;

  private ImageView userImage;
  private TextView username;

  private boolean isCommentsCollapsed = false;

  public RateAndReviewCommentWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    reply = (TextView) itemView.findViewById(R.id.write_reply_btn);
    showHideReplies = (TextView) itemView.findViewById(R.id.show_replies_btn);
    flagHelfull = (Button) itemView.findViewById(R.id.helpful_btn);
    flagNotHelfull = (Button) itemView.findViewById(R.id.not_helpful_btn);

    ratingBar = (AppCompatRatingBar) itemView.findViewById(R.id.rating_bar);
    reviewTitle = (TextView) itemView.findViewById(R.id.comment_title);
    reviewDate = (TextView) itemView.findViewById(R.id.added_date);
    reviewText = (TextView) itemView.findViewById(R.id.comment);

    userImage = (ImageView) itemView.findViewById(R.id.user_icon);
    username = (TextView) itemView.findViewById(R.id.user_name);
  }

  @Override public void bindView(RateAndReviewCommentDisplayable displayable) {
    final Review review = displayable.getPojo().getReview();
    final String appName = displayable.getPojo().getAppName();

    ImageLoader.loadWithCircleTransformAndPlaceHolderAvatarSize(review.getUser().getAvatar(),
        userImage, R.drawable.layer_1);
    username.setText(review.getUser().getName());
    ratingBar.setRating(review.getStats().getRating());
    reviewTitle.setText(review.getTitle());
    reviewText.setText(review.getBody());
    reviewDate.setText(DATE_TIME_U.getTimeDiffString(getContext(), review.getAdded().getTime()));

    reply.setOnClickListener(v -> {
      if (AptoideAccountManager.isLoggedIn()) {
        showCommentPopup(review.getId(), appName, displayable.getCommentAdder());
      } else {
        ShowMessage.asSnack(ratingBar, R.string.you_need_to_be_logged_in, R.string.login,
            snackView -> {
              AptoideAccountManager.openAccountManager(snackView.getContext());
            });
      }
    });

    flagHelfull.setOnClickListener(v -> {
      setReviewRating(review.getId(), true);
    });

    flagNotHelfull.setOnClickListener(v -> {
      setReviewRating(review.getId(), false);
    });

    showHideReplies.setOnClickListener(v -> {
      if (isCommentsCollapsed) {
        loadCommentsForThisReview(review.getId(), FULL_COMMENTS_LIMIT,
            displayable.getCommentAdder());
        showHideReplies.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_up_arrow, 0);
        isCommentsCollapsed = false;
      } else {
        displayable.getCommentAdder().collapseComments();
        showHideReplies.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_down_arrow, 0);
        isCommentsCollapsed = true;
      }
    });

    final Resources.Theme theme = getContext().getTheme();
    final Resources res = getContext().getResources();
    int color =
        getItemId() % 2 == 0 ? R.color.white : R.color.displayable_rate_and_review_background;

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      itemView.setBackgroundColor(res.getColor(color, theme));
    } else {
      itemView.setBackgroundColor(res.getColor(color));
    }
  }

  private Observable<BaseV7Response> submitComment(long reviewId, String commentOnReviewText) {
    return PostCommentForReviewRequest.of(reviewId, commentOnReviewText,
        AptoideAccountManager.getAccessToken(),
        new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
            DataProvider.getContext()).getAptoideClientUUID()).observe();
  }

  private void showCommentPopup(final long reviewId, String appName, CommentAdder commentAdder) {

    CommentDialogWrapper dialogWrapper = new CommentDialogWrapper();
    dialogWrapper.build(getContext(), appName);

    compositeSubscription.add(dialogWrapper.onCommentButton().filter(inputText -> {
      AptoideUtils.SystemU.hideKeyboard(RateAndReviewCommentWidget.this.getContext());
      if (TextUtils.isEmpty(inputText)) {
        dialogWrapper.enableError(AptoideUtils.StringU.getResString(R.string.error_MARG_107));
        return false;
      }
      dialogWrapper.disableError();
      return true;
    }).flatMap(inputText -> submitComment(reviewId, inputText)).subscribe(wsResponse -> {
      if(wsResponse.isOk()) {
        ManagerPreferences.setForceServerRefreshFlag(true);
        commentAdder.collapseComments();
        loadCommentsForThisReview(reviewId, FULL_COMMENTS_LIMIT, commentAdder);
        Logger.d(TAG, "comment to review added");
        ShowMessage.asSnack(getContext(), R.string.comment_submitted);
        dialogWrapper.dismiss();
        return;
      }

      ShowMessage.asSnack(getContext(), R.string.error_occured);

    }, e -> {
      Logger.e(TAG, e);
      ShowMessage.asSnack(getContext(), R.string.error_occured);
    }));

    dialogWrapper.show();
  }

  private void loadCommentsForThisReview(long reviewId, int limit, CommentAdder commentAdder) {
    ListCommentsRequest.of(reviewId, limit, AptoideAccountManager.getAccessToken(),
        new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
            DataProvider.getContext()).getAptoideClientUUID()).execute(listComments -> {
      if (listComments.isOk()) {
        List<Comment> comments = listComments.getDatalist().getList();
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
    flagHelfull.setClickable(false);
    flagNotHelfull.setClickable(false);

    flagHelfull.setVisibility(View.INVISIBLE);
    flagNotHelfull.setVisibility(View.INVISIBLE);

    if (AptoideAccountManager.isLoggedIn()) {
      SetReviewRatingRequest.of(reviewId, positive, AptoideAccountManager.getAccessToken(),
          AptoideAccountManager.getUserEmail(),
          new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
              DataProvider.getContext()).getAptoideClientUUID()).execute(response -> {
        if (response == null) {
          Logger.e(TAG, "empty response");
          return;
        }

        if (response.getError() != null) {
          Logger.e(TAG, response.getError().getDescription());
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
      }, err -> {
        Logger.e(TAG, err);
      }, true);
    }

    ShowMessage.asSnack(flagHelfull, R.string.thank_you_for_your_opinion);
  }
}

/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 09/08/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatRatingBar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.v7.ListCommentsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.PostCommentRequest;
import cm.aptoide.pt.dataprovider.ws.v7.SetReviewRatingRequest;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.model.v7.Comment;
import cm.aptoide.pt.model.v7.Review;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.implementations.RateAndReviewsFragment;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.RateAndReviewCommentDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.BaseWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;

/**
 * Created by sithengineer on 14/07/16.
 */
@Displayables({RateAndReviewCommentDisplayable.class})
public class RateAndReviewCommentWidget extends BaseWidget<RateAndReviewCommentDisplayable> {

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

	@Override
	protected void assignViews(View itemView) {
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

	@Override
	public void bindView(RateAndReviewCommentDisplayable displayable) {
		final Review review = displayable.getPojo().getReview();
		final String appName = displayable.getPojo().getAppName();

		ImageLoader.loadWithCircleTransformAndPlaceHolderAvatarSize(review.getUser().getAvatar(), userImage, R.drawable.layer_1);
		username.setText(review.getUser().getName());
		ratingBar.setRating(review.getStats().getRating());
		reviewTitle.setText(review.getTitle());
		reviewText.setText(review.getBody());
		reviewDate.setText(DATE_TIME_U.getTimeDiffString(getContext(), review.getAdded().getTime()));

		reply.setOnClickListener(v -> {
			if (AptoideAccountManager.isLoggedIn()) {
				showCommentPopup(review.getId(), appName, displayable.getCommentAdder());
			} else {
				ShowMessage.asSnack(ratingBar, R.string.you_need_to_be_logged_in, R.string.login, snackView -> {
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
				loadCommentsForThisReview(review.getId(), FULL_COMMENTS_LIMIT, displayable.getCommentAdder());
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
		int color = getItemId() % 2 == 0 ? R.color.white : R.color.displayable_rate_and_review_background;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			itemView.setBackgroundColor(res.getColor(color, theme));
		} else {
			itemView.setBackgroundColor(res.getColor(color));
		}
	}

	private void showCommentPopup(final long reviewId, String appName, RateAndReviewsFragment.CommentAdder commentAdder) {
		final Context ctx = getContext();
		final View view = LayoutInflater.from(ctx).inflate(R.layout.dialog_comment_on_review, null);

		final TextView titleTextView = (TextView) view.findViewById(R.id.title);
		final TextInputLayout textInputLayout = (TextInputLayout) view.findViewById(R.id.input_layout_title);
		final Button commentBtn = (Button) view.findViewById(R.id.comment_button);
		final Button cancelBtn = (Button) view.findViewById(R.id.cancel_button);

		titleTextView.setText(appName);

		// build rating dialog
		final AlertDialog.Builder builder = new AlertDialog.Builder(ctx).setView(view);
		final AlertDialog dialog = builder.create();

		commentBtn.setOnClickListener(v -> {

			AptoideUtils.SystemU.hideKeyboard(getContext());

			final String commentOnReviewText = textInputLayout.getEditText().getText().toString();

			if (TextUtils.isEmpty(commentOnReviewText)) {
				textInputLayout.setError(AptoideUtils.StringU.getResString(R.string.error_MARG_107));
				return;
			}

			textInputLayout.setErrorEnabled(false);
			dialog.dismiss();

			PostCommentRequest.of(reviewId, commentOnReviewText).execute(response -> {
				dialog.dismiss();
				if (response.isOk()) {
					ManagerPreferences.setForceServerRefreshFlag(true);
					commentAdder.collapseComments();
					loadCommentsForThisReview(reviewId, FULL_COMMENTS_LIMIT, commentAdder);
					Logger.d(TAG, "comment to review added");
					ShowMessage.asSnack(flagHelfull, R.string.comment_submitted);
				} else {
					ShowMessage.asSnack(flagHelfull, R.string.error_occured);
				}
			}, e -> {
				dialog.dismiss();
				Logger.e(TAG, e);
				ShowMessage.asSnack(flagHelfull, R.string.error_occured);
			});
		});

		cancelBtn.setOnClickListener(v -> {
			dialog.dismiss();
		});

		dialog.show();
	}

	private void loadCommentsForThisReview(long reviewId, int limit, RateAndReviewsFragment.CommentAdder commentAdder) {
		ListCommentsRequest.of(reviewId, limit).execute(listComments -> {
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
			SetReviewRatingRequest.of(reviewId, positive).execute(response -> {
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
				Logger.d(TAG, String.format("review %d was marked as %s", reviewId, positive ? "positive" : "negative"));

			}, err -> {
				Logger.e(TAG, err);
			}, true);
		}

		ShowMessage.asSnack(flagHelfull, R.string.thank_you_for_your_opinion);
	}
}

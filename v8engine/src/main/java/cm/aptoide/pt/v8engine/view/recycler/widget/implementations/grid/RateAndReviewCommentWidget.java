/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 29/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.content.res.Resources;
import android.os.Build;
import android.support.v7.widget.AppCompatRatingBar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.v7.SetReviewRatingRequest;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.Review;
import cm.aptoide.pt.utils.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.RateAndReviewCommentDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.BaseWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;

/**
 * Created by sithengineer on 14/07/16.
 */
@Displayables({RateAndReviewCommentDisplayable.class})
public class RateAndReviewCommentWidget extends BaseWidget<RateAndReviewCommentDisplayable> {

	private static final String TAG = RateAndReviewCommentWidget.class.getSimpleName();
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/mm/yyyy", Locale.getDefault());

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
		final Review review = displayable.getPojo();

		ImageLoader.loadWithCircleTransform(review.getUser().getAvatar(), userImage);
		username.setText(review.getUser().getName());

		// TODO: 18/07/16 sithengineer ratingBar.setRating( ?? );
		ratingBar.setVisibility(View.INVISIBLE);

		// TODO: 18/07/16 sithengineer reviewTitle.setText( ?? );
		reviewTitle.setVisibility(View.INVISIBLE);

		reviewText.setText(review.getBody());

		reviewDate.setText(DATE_FORMAT.format(review.getAdded()));

		reply.setOnClickListener(v -> {
			showCommentPopup(review.getId());
		});

		flagHelfull.setOnClickListener(v -> {
			setReviewRating(review.getId(), true);
		});

		flagNotHelfull.setOnClickListener(v -> {
			setReviewRating(review.getId(), false);
		});

		showHideReplies.setOnClickListener(v -> {
			loadCommentsForThisReview(review.getId());
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

	private void showCommentPopup(long reviewId) {
		ShowMessage.asSnack(flagHelfull, "TO DO: write reply");
	}

	private void loadCommentsForThisReview(long reviewId) {
		ShowMessage.asSnack(flagHelfull, "TO DO: show / hide replies");
	}

	private void setReviewRating(long reviewId, boolean positive) {
		flagHelfull.setClickable(false);
		flagNotHelfull.setClickable(false);

		if (AptoideAccountManager.isLoggedIn()) {
			SetReviewRatingRequest.of(reviewId, positive).execute(response -> {

			}, err -> {
				Logger.e(TAG, err);
			}, true);
		}

		ShowMessage.asSnack(flagHelfull, R.string.thank_you_for_your_opinion);
	}
}

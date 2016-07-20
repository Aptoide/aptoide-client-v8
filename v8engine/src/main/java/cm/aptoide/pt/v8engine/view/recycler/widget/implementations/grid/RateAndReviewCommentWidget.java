/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 20/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.support.v7.widget.AppCompatRatingBar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.model.v7.Review;
import cm.aptoide.pt.utils.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.RateAndReviewCommentDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by sithengineer on 14/07/16.
 */
@Displayables({RateAndReviewCommentDisplayable.class})
public class RateAndReviewCommentWidget extends Widget<RateAndReviewCommentDisplayable> {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/mm/yyyy", Locale.getDefault());

	private Button reply;
	private Button flagHelfull;
	private Button flagNotHelfull;
	private Button showHideReplies;

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
		reply = (Button) itemView.findViewById(R.id.write_reply_btn);
		flagHelfull = (Button) itemView.findViewById(R.id.helpful_btn);
		flagNotHelfull = (Button) itemView.findViewById(R.id.not_helpful_btn);
		showHideReplies = (Button) itemView.findViewById(R.id.show_replies_btn);

		ratingBar = (AppCompatRatingBar) itemView.findViewById(R.id.rating_bar);
		reviewTitle = (TextView) itemView.findViewById(R.id.comment_title);
		reviewDate = (TextView) itemView.findViewById(R.id.added_date);
		reviewText = (TextView) itemView.findViewById(R.id.comment);

		userImage = (ImageView) itemView.findViewById(R.id.user_icon);
		username = (TextView) itemView.findViewById(R.id.user_name);
	}

	@Override
	public void bindView(RateAndReviewCommentDisplayable displayable) {
		Review review = displayable.getPojo();

		ImageLoader.loadWithCircleTransform(review.getUser().getAvatar(), userImage);
		username.setText(review.getUser().getName());

		// TODO: 18/07/16 sithengineer ratingBar.setRating( ?? );
		ratingBar.setVisibility(View.INVISIBLE);

		// TODO: 18/07/16 sithengineer reviewTitle.setText( ?? );
		reviewTitle.setVisibility(View.INVISIBLE);

		reviewText.setText(review.getBody());

		reviewDate.setText(DATE_FORMAT.format(review.getAdded()));

		reply.setOnClickListener(v -> {
			ShowMessage.asSnack(v, "TO DO: write reply");
		});

		flagHelfull.setOnClickListener(v -> {
			ShowMessage.asSnack(v, "TO DO: flag as helpful");
		});

		flagNotHelfull.setOnClickListener(v -> {
			ShowMessage.asSnack(v, "TO DO: flag as NOT helpful");
		});

		showHideReplies.setOnClickListener(v -> {
			ShowMessage.asSnack(v, "TO DO: show / hide replies");
		});
	}

	@Override
	public void unbindView() {

	}
}

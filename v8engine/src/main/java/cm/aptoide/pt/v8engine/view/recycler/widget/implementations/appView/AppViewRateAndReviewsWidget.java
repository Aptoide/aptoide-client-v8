/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 25/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatRatingBar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.v7.ListReviewsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.PostReviewRequest;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.model.v7.Review;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.BaseFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.RateAndReviewsFragment;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewRateAndCommentsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import lombok.Setter;

/**
 * Created by sithengineer on 30/06/16.
 */
@Displayables({AppViewRateAndCommentsDisplayable.class})
public class AppViewRateAndReviewsWidget extends Widget<AppViewRateAndCommentsDisplayable> {

	private static final String TAG = AppViewRateAndReviewsWidget.class.getSimpleName();
	private static final Locale LOCALE = Locale.getDefault();
	private static final int MAX_COMMENTS = 3;

	private View emptyReviewsLayout;
	private View ratingLayout;
	private View commentsLayout;

	private TextView usersVoted;
	private TextView ratingValue;
	private AppCompatRatingBar ratingBar;
	private ViewPager topCommentsPager;

	private Button rateThisAppButton;
	private Button rateThisButton;
	private Button readAllButton;

	private TopReviewsAdapter topReviewsAdapter;

	private String appName;
	private String packageName;
	private String storeName;

	private ProgressBar topReviewsProgressBar;

	public AppViewRateAndReviewsWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		emptyReviewsLayout = itemView.findViewById(R.id.empty_reviews_layout);
		ratingLayout = itemView.findViewById(R.id.rating_layout);
		commentsLayout = itemView.findViewById(R.id.comments_layout);

		usersVoted = (TextView) itemView.findViewById(R.id.users_voted);
		ratingValue = (TextView) itemView.findViewById(R.id.rating_value);
		ratingBar = (AppCompatRatingBar) itemView.findViewById(R.id.rating_bar);
		topCommentsPager = (ViewPager) itemView.findViewById(R.id.top_comments_pager);
		rateThisButton = (Button) itemView.findViewById(R.id.rate_this_button);
		readAllButton = (Button) itemView.findViewById(R.id.read_all_button);
		rateThisAppButton = (Button) itemView.findViewById(R.id.rate_this_app_button);

		topReviewsProgressBar = (ProgressBar) itemView.findViewById(R.id.top_reviews_progress_bar);
	}

	@Override
	public void bindView(AppViewRateAndCommentsDisplayable displayable) {
		GetApp pojo = displayable.getPojo();
		GetAppMeta.App app = pojo.getNodes().getMeta().getData();
		GetAppMeta.Stats stats = app.getStats();

		appName = app.getName();
		packageName = app.getPackageName();
		storeName = app.getStore().getName();

		usersVoted.setText(String.format(LOCALE, "%d", stats.getDownloads()));

		float ratingAvg = stats.getRating().getAvg();
		ratingValue.setText(String.format(LOCALE, "%.1f", ratingAvg));
		ratingBar.setRating(ratingAvg);

		View.OnClickListener rateOnClickListener = v -> {
			if (AptoideAccountManager.isLoggedIn()) {
				showRateDialog();
			} else {
				ShowMessage.asSnack(ratingBar, R.string.you_need_to_be_logged_in, R.string.login, snackView -> {
					AptoideAccountManager.openAccountManager(snackView.getContext());
				});
			}
		};
		rateThisButton.setOnClickListener(rateOnClickListener);
		ratingLayout.setOnClickListener(rateOnClickListener);

		View.OnClickListener commentsOnClickListener = v -> {
			((FragmentShower) getContext()).pushFragmentV4(RateAndReviewsFragment.newInstance(app.getId(), app.getStore().getName(), app.getPackageName()));
		};
		readAllButton.setOnClickListener(commentsOnClickListener);
		commentsLayout.setOnClickListener(commentsOnClickListener);
		rateThisAppButton.setOnClickListener(commentsOnClickListener);

		topReviewsAdapter = new TopReviewsAdapter(getContext().getSupportFragmentManager());
		topCommentsPager.setAdapter(topReviewsAdapter);
		loadTopComments(app.getStore().getName(), app.getPackageName());
	}

	private void showRateDialog() {
		final Context ctx = getContext();
		final View view = LayoutInflater.from(ctx).inflate(R.layout.dialog_rate_app, null);

		final TextView titleTextView = (TextView) view.findViewById(R.id.title);
		final AppCompatRatingBar reviewRatingBar = (AppCompatRatingBar) view.findViewById(R.id.rating_bar);
		final EditText titleEditText = (EditText) view.findViewById(R.id.input_title);
		final EditText reviewEditText = (EditText) view.findViewById(R.id.input_review);

		titleTextView.setText(String.format(LOCALE, ctx.getString(R.string.rate_app), appName));

		// build rating dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx).setView(view);
		DialogInterface.OnClickListener clickListener = (dialog, which) -> {
			if (which == DialogInterface.BUTTON_POSITIVE) {

				final String reviewTitle = titleEditText.getText().toString();
				final String reviewText = reviewEditText.getText().toString();
				final int reviewRating = Math.round(reviewRatingBar.getRating());

				PostReviewRequest.of(storeName, packageName, reviewTitle, reviewText, reviewRating).execute(response -> {

					if (response.getError() != null) {
						Logger.e(TAG, response.getError().toString());
						return;
					}

					List<BaseV7Response.Error> errors = response.getErrors();
					if (errors != null && !errors.isEmpty()) {
						for (final BaseV7Response.Error error : errors) {
							Logger.e(TAG, error.toString());
						}
						return;
					}

					ManagerPreferences.setForceServerRefreshFlag(true);
					Logger.d(TAG, "review added");

				}, e -> {
					Logger.e(TAG, e);
					ShowMessage.asSnack(ratingLayout, R.string.error_occured);
				});

				ShowMessage.asSnack(ratingLayout, R.string.thank_you_for_your_opinion);
			} else if (which == DialogInterface.BUTTON_NEGATIVE) {
				// do nothing.
			}
			dialog.dismiss();
		};
		builder.setPositiveButton(R.string.rate, clickListener);
		builder.setCancelable(true).setNegativeButton(R.string.cancel, clickListener);

		// create and show rating dialog
		builder.create().show();
	}

	private void scheduleAnimations() {
		if (ManagerPreferences.getAnimationsEnabledStatus() && topReviewsAdapter.getCount() > 1) {
			for (int i = 0 ; i < topReviewsAdapter.getCount() - 1 ; ++i) {
				final int count = i;
				topCommentsPager.postDelayed(() -> {
					topCommentsPager.setCurrentItem(count, true);
				}, (count + 1) * 1200);
			}
		} else {
			Logger.w(TAG, "Animations are disabled");
		}
	}

	public void loadTopComments(String storeName, String packageName) {
		ListReviewsRequest.ofTopReviews(storeName, packageName, MAX_COMMENTS).execute(listReviews -> {
			topReviewsProgressBar.setVisibility(View.GONE);
			topCommentsPager.setVisibility(View.VISIBLE);
					List<Review> reviews = listReviews.getDatalist().getList();
					if (reviews == null || reviews.isEmpty()) {
						emptyReviewsLayout.setVisibility(View.VISIBLE);
						ratingLayout.setVisibility(View.GONE);
						commentsLayout.setVisibility(View.GONE);
						topReviewsAdapter.setReviews(null);
						topReviewsAdapter.notifyDataSetChanged();
						return;
					}

					topReviewsAdapter.setReviews(listReviews.getDatalist().getList());
					topReviewsAdapter.notifyDataSetChanged();
					scheduleAnimations();
				}, e -> {
					emptyReviewsLayout.setVisibility(View.VISIBLE);
					ratingLayout.setVisibility(View.GONE);
					commentsLayout.setVisibility(View.GONE);
			topReviewsProgressBar.setVisibility(View.GONE);

					topReviewsAdapter.setReviews(null);
					topReviewsAdapter.notifyDataSetChanged();
					Logger.e(TAG, e);
				}, true // bypass cache flag
		);
	}

	private static final class TopReviewsAdapter extends FragmentPagerAdapter {

		@Setter private List<Review> reviews;

		public TopReviewsAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return reviews == null ? 0 : reviews.size();
		}

		@Override
		public Fragment getItem(int position) {
			if (reviews != null && position < reviews.size()) {
				return MiniTopReviewFragment.newInstance(reviews.get(position));
			}
			return new MiniTopReviewFragment(); // FIXME: 15/07/16 sithengineer this shouldn't happen
		}
	}

	public static final class MiniTopReviewFragment extends BaseFragment {

		private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

		private Review review;

		private ImageView userIcon;
		private RatingBar ratingBar;
		private TextView commentTitle;
		private TextView userName;
		private TextView addedDate;
		private TextView commentText;

		public MiniTopReviewFragment() {
		}

		public static MiniTopReviewFragment newInstance(Review review) {
			MiniTopReviewFragment fragment = new MiniTopReviewFragment();
			fragment.review = review;
			return fragment;
		}

		@Override
		public int getContentViewId() {
			return R.layout.mini_top_comment;
		}

		@Override
		public void bindViews(@Nullable View view) {
			userIcon = (ImageView) view.findViewById(R.id.user_icon);
			ratingBar = (RatingBar) view.findViewById(R.id.rating_bar);
			commentTitle = (TextView) view.findViewById(R.id.comment_title);
			userName = (TextView) view.findViewById(R.id.user_name);
			addedDate = (TextView) view.findViewById(R.id.added_date);
			commentText = (TextView) view.findViewById(R.id.comment);
		}

		@Override
		public void setupViews() {
			if (review == null) {
				return;
			}
			ImageLoader.load(review.getUser().getAvatar(), userIcon);
			userName.setText(review.getUser().getName());
			//ratingBar.setRating( ?? );
			//commentTitle.setText( ?? );
			commentText.setText(review.getBody());
			addedDate.setText(SIMPLE_DATE_FORMAT.format(review.getAdded()));
		}
	}
}

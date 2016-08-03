/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 03/08/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatRatingBar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

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
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.BaseFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.RateAndReviewsFragment;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewRateAndCommentsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

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

	private Button rateThisButton;
	private Button rateThisButtonLarge;
	private Button readAllButton;

	private ViewPager topReviewsPager;

	private String appName;
	private String packageName;
	private String storeName;

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
		rateThisButton = (Button) itemView.findViewById(R.id.rate_this_button);
		rateThisButtonLarge = (Button) itemView.findViewById(R.id.rate_this_button2);
		readAllButton = (Button) itemView.findViewById(R.id.read_all_button);

		topReviewsPager = (ViewPager) itemView.findViewById(R.id.top_comments_pager);
	}

	@Override
	public void bindView(AppViewRateAndCommentsDisplayable displayable) {
		GetApp pojo = displayable.getPojo();
		GetAppMeta.App app = pojo.getNodes().getMeta().getData();
		GetAppMeta.Stats stats = app.getStats();

		appName = app.getName();
		packageName = app.getPackageName();
		storeName = app.getStore().getName();

		usersVoted.setText(AptoideUtils.StringU.withSuffix(stats.getDownloads()));

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
		rateThisButtonLarge.setOnClickListener(rateOnClickListener);
		ratingLayout.setOnClickListener(rateOnClickListener);
		//rateThisAppButton.setOnClickListener(rateOnClickListener);

		View.OnClickListener commentsOnClickListener = v -> {
			((FragmentShower) getContext()).pushFragmentV4(RateAndReviewsFragment.newInstance(app.getId(), app.getStore().getName(), app.getPackageName()));
		};
		readAllButton.setOnClickListener(commentsOnClickListener);
		commentsLayout.setOnClickListener(commentsOnClickListener);
		loadTopReviews(app.getStore().getName(), app.getPackageName());
	}

	@Override
	public void onViewAttached() {
	}

	@Override
	public void onViewDetached() {
	}

	private void showRateDialog() {
		final Context ctx = getContext();
		final View view = LayoutInflater.from(ctx).inflate(R.layout.dialog_rate_app, null);

		final TextView titleTextView = (TextView) view.findViewById(R.id.title);
		final AppCompatRatingBar reviewRatingBar = (AppCompatRatingBar) view.findViewById(R.id.rating_bar);
		final EditText titleEditText = (EditText) view.findViewById(R.id.input_title);
		final EditText reviewEditText = (EditText) view.findViewById(R.id.input_review);
		final Button cancelBtn = (Button) view.findViewById(R.id.cancel_button);
		final Button rateBtn = (Button) view.findViewById(R.id.rate_button);

		titleTextView.setText(String.format(LOCALE, ctx.getString(R.string.rate_app), appName));

		AlertDialog.Builder builder = new AlertDialog.Builder(ctx).setView(view);
		AlertDialog dialog = builder.create();

		cancelBtn.setOnClickListener(v -> dialog.dismiss());
		rateBtn.setOnClickListener(v -> {
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
				dialog.dismiss();
				Logger.d(TAG, "review added");
				ShowMessage.asSnack(ratingLayout, R.string.thank_you_for_your_opinion);
			}, e -> {
				Logger.e(TAG, e);
				ShowMessage.asSnack(ratingLayout, R.string.error_occured);
			});
		});

		// create and show rating dialog
		dialog.show();
	}

	private void scheduleAnimations() {
		final int topReviewsCount = topReviewsPager.getAdapter().getCount();
		if (topReviewsCount > 1) {
			for (int i = 0 ; i < topReviewsCount - 1 ; ++i) {
				final int count = i + 1;
				topReviewsPager.postDelayed(() -> {
					topReviewsPager.setCurrentItem(count);
				}, count * 1000);
			}
		} else {
			Logger.w(TAG, "Not enough top reviews to do paging animation.");
		}
	}

	public void loadTopReviews(String storeName, String packageName) {
		ListReviewsRequest.ofTopReviews(storeName, packageName, MAX_COMMENTS).execute(listReviews -> {

					List<Review> reviews = listReviews.getDatalist().getList();
					if (reviews == null || reviews.isEmpty()) {
						topReviewsPager.setAdapter(new TopReviewsAdapter(getContext().getFragmentManager(), null));
						loadedData(false);
						return;
					}

					loadedData(true);
					topReviewsPager.setAdapter(new TopReviewsAdapter(getContext().getFragmentManager(), listReviews.getDatalist().getList()));
					scheduleAnimations();
					topReviewsPager.invalidate();
				}, e -> {
					loadedData(false);
					topReviewsPager.setAdapter(new TopReviewsAdapter(getContext().getFragmentManager(), null));
					Logger.e(TAG, e);

				}, true // bypass cache flag
		);
	}

	private void loadedData(boolean hasData) {

		if (hasData) {
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
		}
	}

	private static final class TopReviewsAdapter extends FragmentPagerAdapter {

		private final List<Review> reviews;

		public TopReviewsAdapter(android.app.FragmentManager fm, List<Review> reviews) {
			super(fm);
			this.reviews = reviews;
		}

		@Override
		public int getCount() {
			return reviews == null ? 0 : reviews.size();
		}

		@Override
		public android.app.Fragment getItem(int position) {
			if (reviews != null && position < reviews.size()) {
				return MiniTopReviewFragment.newInstance(reviews.get(position));
			}
			throw new IllegalStateException("Top Review Item doesn't exist for position " + position);
		}
	}

	public static final class MiniTopReviewFragment extends BaseFragment {

		private static final AptoideUtils.DateTimeU DATE_TIME_U = AptoideUtils.DateTimeU.getInstance();
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
		public void bindViews(View view) {
			userIcon = (ImageView) view.findViewById(R.id.user_icon);
			ratingBar = (RatingBar) view.findViewById(R.id.rating_bar);
			commentTitle = (TextView) view.findViewById(R.id.comment_title);
			userName = (TextView) view.findViewById(R.id.user_name);
			addedDate = (TextView) view.findViewById(R.id.added_date);
			commentText = (TextView) view.findViewById(R.id.comment);
		}

		@Override
		public void setupViews() {
			ImageLoader.loadWithCircleTransformAndPlaceHolder(review.getUser().getAvatar(), userIcon, R.drawable.layer_1);
			userName.setText(review.getUser().getName());
			ratingBar.setRating(review.getStats().getRating());
			commentTitle.setText(review.getTitle());
			commentText.setText(review.getBody());
			addedDate.setText(DATE_TIME_U.getTimeDiffString(getActivity(), review.getAdded().getTime()));
		}
	}
}

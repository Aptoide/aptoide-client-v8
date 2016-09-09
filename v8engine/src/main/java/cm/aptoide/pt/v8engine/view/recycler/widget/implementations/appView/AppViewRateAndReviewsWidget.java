/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 29/08/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView;

import android.content.Context;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import cm.aptoide.pt.dataprovider.ws.v7.ListReviewsRequest;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.model.v7.Review;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.implementations.RateAndReviewsFragment;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.util.DialogUtils;
import cm.aptoide.pt.v8engine.util.LinearLayoutManagerWithSmootheScroller;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewRateAndCommentsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by sithengineer on 30/06/16.
 */
@Displayables({AppViewRateAndCommentsDisplayable.class})
public class AppViewRateAndReviewsWidget extends Widget<AppViewRateAndCommentsDisplayable> {

	private static final String TAG = AppViewRateAndReviewsWidget.class.getSimpleName();
	private static final int MAX_COMMENTS = 3;

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

	public AppViewRateAndReviewsWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
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
		topReviewsProgress = (ContentLoadingProgressBar) itemView.findViewById(R.id.top_comments_progress);
	}

	@Override
	public void bindView(AppViewRateAndCommentsDisplayable displayable) {
		GetApp pojo = displayable.getPojo();
		GetAppMeta.App app = pojo.getNodes().getMeta().getData();
		GetAppMeta.Stats stats = app.getStats();

		appName = app.getName();
		packageName = app.getPackageName();
		storeName = app.getStore().getName();

		usersToVote = stats.getRating().getTotal();
		usersVotedTextView.setText(AptoideUtils.StringU.withSuffix(usersToVote));

		float ratingAvg = stats.getRating().getAvg();
		ratingValue.setText(String.format(AptoideUtils.LocaleU.DEFAULT, "%.1f", ratingAvg));
		ratingBar.setRating(ratingAvg);

		View.OnClickListener rateOnClickListener = v -> {
			DialogUtils.showRateDialog(getContext(), appName, packageName, storeName, this::loadReviews);
		};

		rateThisButton.setOnClickListener(rateOnClickListener);
		rateThisButtonLarge.setOnClickListener(rateOnClickListener);
		ratingLayout.setOnClickListener(rateOnClickListener);
		//rateThisAppButton.setOnClickListener(rateOnClickListener);

		View.OnClickListener commentsOnClickListener = v -> {
			((FragmentShower) getContext()).pushFragmentV4(RateAndReviewsFragment.newInstance(app.getId(), app.getName(), app.getStore()
					.getName(), app.getPackageName()));
		};
		readAllButton.setOnClickListener(commentsOnClickListener);
		commentsLayout.setOnClickListener(commentsOnClickListener);

		LinearLayoutManagerWithSmootheScroller layoutManager = new LinearLayoutManagerWithSmootheScroller(getContext(), LinearLayoutManager.HORIZONTAL, false);
		topReviewsList.setLayoutManager(layoutManager);
		topReviewsList.setNestedScrollingEnabled(false); // because otherwise the AppBar won't be collapsed

		loadReviews();
	}

	@Override
	public void onViewAttached() {
	}

	@Override
	public void onViewDetached() {
	}

	private void loadReviews() {
		loadTopReviews(storeName, packageName);
	}

	private void scheduleAnimations() {
		final int topReviewsCount = topReviewsList.getLayoutManager().getItemCount();
		if (topReviewsCount > 1) {
			for (int i = 0 ; i < topReviewsCount - 1 ; ++i) {
				final int count = i + 1;
				topReviewsList.postDelayed(() -> {
					topReviewsList.smoothScrollToPosition(count);
				}, count * 1700);
			}
		} else {
			Logger.w(TAG, "Not enough top reviews to do paging animation.");
		}
	}

	public void loadTopReviews(String storeName, String packageName) {
		ListReviewsRequest.ofTopReviews(storeName, packageName, MAX_COMMENTS).execute(listReviews -> {

					List<Review> reviews = listReviews.getDatalist().getList();
					if (reviews == null || reviews.isEmpty()) {
						topReviewsList.setAdapter(new TopReviewsAdapter(getContext()));
						loadedData(false);
						return;
					}

					loadedData(true);
			final List<Review> list = listReviews.getDatalist().getList();
			topReviewsList.setAdapter(new TopReviewsAdapter(getContext(), list.toArray(new Review[list.size()])));
					scheduleAnimations();
				}, e -> {
					loadedData(false);
			topReviewsList.setAdapter(new TopReviewsAdapter(getContext()));
					Logger.e(TAG, e);

				}, true // bypass cache flag
		);
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

	private static final class TopReviewsAdapter extends RecyclerView.Adapter<MiniTopReviewViewHolder> {

		private final Review[] reviews;
		private final Context context;

		public TopReviewsAdapter(Context context) {
			this(context, null);
		}

		public TopReviewsAdapter(Context context, Review[] reviews) {
			this.reviews = reviews;
			this.context = context;
		}

		@Override
		public MiniTopReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			LayoutInflater inflater = LayoutInflater.from(context);
			return new MiniTopReviewViewHolder(inflater.inflate(MiniTopReviewViewHolder.LAYOUT_ID, parent, false));
		}

		@Override
		public void onBindViewHolder(MiniTopReviewViewHolder holder, int position) {
			holder.setup(context, reviews[position]);
		}

		@Override
		public int getItemCount() {
			return reviews == null ? 0 : reviews.length;
		}
	}

	public static final class MiniTopReviewViewHolder extends RecyclerView.ViewHolder {

		public static final int LAYOUT_ID = R.layout.mini_top_comment;

		private static final AptoideUtils.DateTimeU DATE_TIME_U = AptoideUtils.DateTimeU.getInstance();

		private ImageView userIconImageView;
		private RatingBar ratingBar;
		private TextView commentTitle;
		private TextView userName;
		private TextView addedDate;
		private TextView commentText;

		public MiniTopReviewViewHolder(View itemView) {
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

		public void setup(Context context, Review review) {
			ImageLoader.loadWithCircleTransformAndPlaceHolderAvatarSize(review.getUser().getAvatar(), userIconImageView, R.drawable.layer_1);
			userName.setText(review.getUser().getName());
			ratingBar.setRating(review.getStats().getRating());
			commentTitle.setText(review.getTitle());
			commentText.setText(review.getBody());
			addedDate.setText(DATE_TIME_U.getTimeDiffString(context, review.getAdded().getTime()));
		}
	}
}

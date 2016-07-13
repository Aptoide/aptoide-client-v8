/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 11/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.Comment;
import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
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

	private TextView usersVoted;
	private TextView ratingValue;
	private RatingBar ratingBar;
	private ViewPager topCommentsPager;

	private Button rateThisButton;
	private Button readAllButton;

	private TopReviewsAdapter topReviewsAdapter;
	private Handler handler;

	public AppViewRateAndReviewsWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		usersVoted = (TextView) itemView.findViewById(R.id.users_voted);
		ratingValue = (TextView) itemView.findViewById(R.id.rating_value);
		ratingBar = (RatingBar) itemView.findViewById(R.id.rating_bar);
		topCommentsPager = (ViewPager) itemView.findViewById(R.id.top_comments_pager);
		rateThisButton = (Button) itemView.findViewById(R.id.rate_this_button);
		readAllButton = (Button) itemView.findViewById(R.id.read_all_button);

		handler = new Handler(Looper.myLooper());
	}

	@Override
	public void bindView(AppViewRateAndCommentsDisplayable displayable) {
		GetApp pojo = displayable.getPojo();
		GetAppMeta.App app = pojo.getNodes().getMeta().getData();
		GetAppMeta.Stats stats = app.getStats();

		usersVoted.setText(String.format(LOCALE, "%d", stats.getDownloads()));

		float ratingAvg = stats.getRating().getAvg();
		ratingValue.setText(String.format(LOCALE, "%.1f", ratingAvg));
		ratingBar.setRating(ratingAvg);

		showTopComments(app.getId());
		rateThisButton.setOnClickListener(v -> {

			// TODO
			ShowMessage.asSnack(v, "TO DO: rate this app");
		});

		readAllButton.setOnClickListener(v -> {
			((FragmentShower) getContext()).pushFragmentV4(RateAndReviewsFragment.newInstance(app.getId()));
		});
	}

	private void showTopComments(long appId) {
		topReviewsAdapter = new TopReviewsAdapter(getContext().getSupportFragmentManager(), appId);
		topCommentsPager.setAdapter(topReviewsAdapter);
		scheduleAnimations();
	}

	private void scheduleAnimations() {
		if (ManagerPreferences.getAnimationsEnabledStatus()) {
			for (int i = 0 ; i < topReviewsAdapter.getCount() ; ++i) {
				final int count = i;
				handler.postDelayed(() -> {
					topCommentsPager.setCurrentItem(count, true);
				}, (count + 1) * 1200);
			}
		} else {
			Logger.w(TAG, "Animations are disabled");
		}
	}

	private static final class TopReviewsAdapter extends FragmentPagerAdapter {

		private static final int MAX_COMMENTS = 3;
		private long appId;
		private List<Comment> comments;

		public TopReviewsAdapter(FragmentManager fm, long appId) {
			super(fm);
			this.appId = appId;
			loadTopComments();
		}

		private void loadTopComments() {

			// TODO fetch top comments

		}

		@Override
		public int getCount() {
			return MAX_COMMENTS;
		}

		@Override
		public Fragment getItem(int position) {
			return new MiniTopCommentFragment(null);//comments.get(position));
		}
	}

	private static final class MiniTopCommentFragment extends BaseFragment {

		private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

		private final Comment comment;

		private ImageView userIcon;
		private RatingBar ratingBar;
		private TextView commentTitle;
		private TextView userName;
		private TextView addedDate;
		private TextView commentText;

		public MiniTopCommentFragment(Comment comment) {
			this.comment = comment;
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
			if (comment == null) {
				return;
			}
			ImageLoader.load(comment.getUser().getAvatar(), userIcon);
			userName.setText(comment.getUser().getName());
			//ratingBar.setRating( ?? );
			//commentTitle.setText( ?? );
			commentText.setText(comment.getBody());
			addedDate.setText(SIMPLE_DATE_FORMAT.format(comment.getAdded()));
		}
	}
}

/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 13/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
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
public class AppViewRateAndCommentsWidget extends Widget<AppViewRateAndCommentsDisplayable> {

	private static final String TAG = AppViewRateAndCommentsWidget.class.getSimpleName();
	private static final Locale LOCALE = Locale.getDefault();

	private View ratingLayout;
	private View commentsLayout;

	private TextView usersVoted;
	private TextView ratingValue;
	private AppCompatRatingBar ratingBar;
	private ViewPager topCommentsPager;

	private Button rateThisButton;
	private Button readAllButton;

	private TopCommentAdapter topCommentAdapter;
	private Handler handler;

	private String appName;

	public AppViewRateAndCommentsWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		ratingLayout = itemView.findViewById(R.id.rating_layout);
		commentsLayout = itemView.findViewById(R.id.comments_layout);
		usersVoted = (TextView) itemView.findViewById(R.id.users_voted);
		ratingValue = (TextView) itemView.findViewById(R.id.rating_value);
		ratingBar = (AppCompatRatingBar) itemView.findViewById(R.id.rating_bar);
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

		appName = app.getName();

		usersVoted.setText(String.format(LOCALE, "%d", stats.getDownloads()));

		float ratingAvg = stats.getRating().getAvg();
		ratingValue.setText(String.format(LOCALE, "%.1f", ratingAvg));
		ratingBar.setRating(ratingAvg);

		showTopComments(app.getId());

		View.OnClickListener rateOnClickListener = v -> {
			showRateDialog();
		};
		rateThisButton.setOnClickListener(rateOnClickListener);
		ratingLayout.setOnClickListener(rateOnClickListener);

		View.OnClickListener commentsOnClickListener = v -> {
			((FragmentShower) getContext()).pushFragmentV4(RateAndReviewsFragment.newInstance(app.getId()));
		};
		readAllButton.setOnClickListener(commentsOnClickListener);
		commentsLayout.setOnClickListener(commentsOnClickListener);
	}

	private void showRateDialog() {
		final Context ctx = getContext();
		final View view = LayoutInflater.from(ctx).inflate(R.layout.dialog_rate_app, null);

		final TextView title = (TextView) view.findViewById(R.id.title);
		final AppCompatRatingBar ratingBar = (AppCompatRatingBar) view.findViewById(R.id.rating_bar);
		final EditText titleText = (EditText) view.findViewById(R.id.input_title);
		final EditText reviewText = (EditText) view.findViewById(R.id.input_review);

		title.setText(String.format(LOCALE, ctx.getString(R.string.rate_app), appName));

		// build review text hint in runtime using spans and different text styles / colors
		/*
		final String reviewLabel = ctx.getString(R.string.review);
		final String optionalLabel = "(" + ctx.getString(R.string.optional)+")";

		SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
		spannableStringBuilder.append(reviewLabel);
		spannableStringBuilder.append(" ");
		SpannableString spannableString = new SpannableString(optionalLabel);
		int color;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			color = ctx.getColor(R.color.medium_custom_gray);
		} else {
			color = ctx.getResources().getColor(R.color.medium_custom_gray);
		}

		spannableString.setSpan(new ForegroundColorSpan(color), 0, optionalLabel.length(), Spannable
				.SPAN_EXCLUSIVE_EXCLUSIVE);
		spannableStringBuilder.append(spannableString);

		reviewText.setHint(spannableStringBuilder);
		*/

		// build rating dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx).setView(view);
		DialogInterface.OnClickListener clickListener = (dialog, which) -> {
			if (which == DialogInterface.BUTTON_POSITIVE) {

				// TODO call ws with rating result
				ShowMessage.asSnack(ratingLayout, "TO DO: call ws with rating result");
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

	private void showTopComments(long appId) {
		topCommentAdapter = new TopCommentAdapter(getContext().getSupportFragmentManager(), appId);
		topCommentsPager.setAdapter(topCommentAdapter);
		scheduleAnimations();
	}

	private void scheduleAnimations() {
		if (ManagerPreferences.getAnimationsEnabledStatus()) {
			for (int i = 0 ; i < topCommentAdapter.getCount() - 1 ; ++i) {
				final int count = i;
				handler.postDelayed(() -> {
					if (topCommentsPager.getVisibility() == View.VISIBLE) {
						topCommentsPager.arrowScroll(View.FOCUS_LEFT);
					}
				}, (count + 1) * 1200);
			}
		} else {
			Logger.w(TAG, "Animations are disabled");
		}
	}

	private static final class TopCommentAdapter extends FragmentPagerAdapter {

		private static final int MAX_COMMENTS = 3;
		private long appId;
		private List<Comment> comments;

		public TopCommentAdapter(FragmentManager fm, long appId) {
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

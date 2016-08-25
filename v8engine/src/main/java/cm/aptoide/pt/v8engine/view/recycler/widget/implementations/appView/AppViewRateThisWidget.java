/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 15/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.Locale;

import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.utils.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewRateThisDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by sithengineer on 04/05/16.
 */
@Deprecated
@Displayables({AppViewRateThisDisplayable.class})
public class AppViewRateThisWidget extends Widget<AppViewRateThisDisplayable> {

	private static final String TAG = AppViewRateThisWidget.class.getSimpleName();

	private RatingBar bigRateThisAppBar;
	private ComunityRatingIndicator comunityRatingIndicator;
	private FlagThisApp flagThisApp;
	private View manualReviewedMessageLayout;

	public AppViewRateThisWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		bigRateThisAppBar = (RatingBar) itemView.findViewById(R.id.ratingbar_appview);
		flagThisApp = new FlagThisApp(itemView);
		manualReviewedMessageLayout = itemView.findViewById(R.id.manual_reviewed_message_layout);
		comunityRatingIndicator = new ComunityRatingIndicator(itemView);
	}

	@Override
	public void bindView(AppViewRateThisDisplayable displayable) {
		final GetApp pojo = displayable.getPojo();

		try {

			GetAppMeta.App app = pojo.getNodes().getMeta().getData();
			bigRateThisAppBar.setRating(app.getStats().getRating().getAvg());
			bigRateThisAppBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
				ShowMessage.asSnack(ratingBar, "TO DO");
				// TODO
			});

			if (GetAppMeta.GetAppMetaFile.Flags.GOOD.equalsIgnoreCase(app.getFile().getFlags().getReview())) {
				manualReviewedMessageLayout.setVisibility(View.GONE);
				flagThisApp.setVisibility(View.VISIBLE);
			} else {
				manualReviewedMessageLayout.setVisibility(View.VISIBLE);
				flagThisApp.setVisibility(View.GONE);
			}
		} catch (Exception ex) {
			Logger.e(TAG, ex);
		}

		try {
			comunityRatingIndicator.bindView(pojo);
		} catch (Exception ex) {
			Logger.e(TAG, ex);
		}

		try {
			flagThisApp.bindView(pojo);
		} catch (Exception ex) {
			Logger.e(TAG, ex);
		}
	}

	@Override
	public void onViewAttached() {

	}

	@Override
	public void onViewDetached() {

	}

	private static final class FlagThisApp {

		private Button flagThisAppBtn;
		private View flagsLayout;
		private TextView numberGoodFlags;
		private TextView numberLicenceFlags;
		private TextView numberFakeFlags;
		private TextView numberFreezeFlags;
		private TextView numberVirusFlags;

		public FlagThisApp(View view) {
			flagsLayout = view.findViewById(R.id.flags_layout);
			flagThisAppBtn = (Button) view.findViewById(R.id.btn_flag_this_app);
			numberGoodFlags = (TextView) view.findViewById(R.id.number_good_flags);
			numberLicenceFlags = (TextView) view.findViewById(R.id.number_licence_flags);
			numberFakeFlags = (TextView) view.findViewById(R.id.number_fake_flags);
			numberFreezeFlags = (TextView) view.findViewById(R.id.number_freeze_flags);
			numberVirusFlags = (TextView) view.findViewById(R.id.number_virus_flags);
		}

		public void bindView(GetApp getApp) {
			flagThisAppBtn.setOnClickListener(v -> {
				ShowMessage.asSnack(v, "TO DO");
				// TODO
			});

			try {
				GetAppMeta.GetAppMetaFile metaFile = getApp.getNodes().getMeta().getData().getFile();
				GetAppMeta.GetAppMetaFile.Flags flags = metaFile.getFlags();

				for (final GetAppMeta.GetAppMetaFile.Flags.Vote vote : flags.getVotes()) {
					bindVoteView(vote);
				}
			} catch (Exception e) {
				Logger.e(TAG, e);
			}
		}

		private void bindVoteView(GetAppMeta.GetAppMetaFile.Flags.Vote vote) {
			String typeText = String.format(Locale.getDefault(), "%s", vote.getCount());
			switch (vote.getType()) {
				case FAKE:
					numberFakeFlags.setText(typeText);
					break;

				case FREEZE:
					numberFreezeFlags.setText(typeText);
					break;

				case GOOD:
					numberGoodFlags.setText(typeText);
					break;

				case LICENSE:
					numberLicenceFlags.setText(typeText);
					break;

				case VIRUS:
					numberVirusFlags.setText(typeText);
					break;

				default:
					// do nothing
					break;
			}
		}

		public void setVisibility(int visible) {
			flagsLayout.setVisibility(visible);
			flagThisAppBtn.setVisibility(visible);
		}
	}

	private static final class ComunityRatingIndicator {

		private CommunityRatingCard ratingCard;
		private ComunityRatingBar ratingBar;

		public ComunityRatingIndicator(View view) {
			View ratingCardView = view.findViewById(R.id.ratingCard);
			if (ratingCardView != null) {
				ratingCard = new CommunityRatingCard(ratingCardView);
			}
			ratingBar = new ComunityRatingBar(view);
		}

		public void bindView(GetApp getApp) {
			if (ratingCard != null) {
				ratingCard.bindView(getApp);
			}
			if (ratingBar != null) {
				ratingBar.bindView(getApp);
			}
		}
	}

	private static final class CommunityRatingCard {

		private TextView avgRating;
		private RatingBar avgRatingBar;
		private TextView nrRates;

		public CommunityRatingCard(View view) {
			avgRating = (TextView) view.findViewById(R.id.appview_avg_rating);
			avgRatingBar = (RatingBar) view.findViewById(R.id.appview_rating_bar_avg);
			nrRates = (TextView) view.findViewById(R.id.tv_number_of_rates);
		}

		public void bindView(GetApp getApp) {
			GetAppMeta.Stats.Rating rating = getApp.getNodes().getMeta().getData().getStats().getRating();

			avgRating.setText(String.format(Locale.getDefault(), "%.1f", rating.getAvg()));
			avgRatingBar.setRating(rating.getAvg());
			nrRates.setText(String.format(Locale.getDefault(), "%d", rating.getTotal()));
		}
	}

	private static final class ComunityRatingBar {

		private ImageView appview_rating_bar_star5;
		private ProgressBar appview_rating_bar5;
		private TextView appview_rating_bar_rating_number5;

		private ImageView appview_rating_bar_star4;
		private ProgressBar appview_rating_bar4;
		private TextView appview_rating_bar_rating_number4;

		private ImageView appview_rating_bar_star3;
		private ProgressBar appview_rating_bar3;
		private TextView appview_rating_bar_rating_number3;

		private ImageView appview_rating_bar_star2;
		private ProgressBar appview_rating_bar2;
		private TextView appview_rating_bar_rating_number2;

		private ImageView appview_rating_bar_star1;
		private ProgressBar appview_rating_bar1;
		private TextView appview_rating_bar_rating_number1;

		public ComunityRatingBar(View view) {
			appview_rating_bar_star5 = (ImageView) view.findViewById(R.id.appview_rating_bar_star5);
			appview_rating_bar5 = (ProgressBar) view.findViewById(R.id.appview_rating_bar5);
			appview_rating_bar_rating_number5 = (TextView) view.findViewById(R.id.appview_rating_bar_rating_number5);

			appview_rating_bar_star4 = (ImageView) view.findViewById(R.id.appview_rating_bar_star4);
			appview_rating_bar4 = (ProgressBar) view.findViewById(R.id.appview_rating_bar4);
			appview_rating_bar_rating_number4 = (TextView) view.findViewById(R.id.appview_rating_bar_rating_number4);

			appview_rating_bar_star3 = (ImageView) view.findViewById(R.id.appview_rating_bar_star3);
			appview_rating_bar3 = (ProgressBar) view.findViewById(R.id.appview_rating_bar3);
			appview_rating_bar_rating_number3 = (TextView) view.findViewById(R.id.appview_rating_bar_rating_number3);

			appview_rating_bar_star2 = (ImageView) view.findViewById(R.id.appview_rating_bar_star2);
			appview_rating_bar2 = (ProgressBar) view.findViewById(R.id.appview_rating_bar2);
			appview_rating_bar_rating_number2 = (TextView) view.findViewById(R.id.appview_rating_bar_rating_number2);

			appview_rating_bar_star1 = (ImageView) view.findViewById(R.id.appview_rating_bar_star1);
			appview_rating_bar1 = (ProgressBar) view.findViewById(R.id.appview_rating_bar1);
			appview_rating_bar_rating_number1 = (TextView) view.findViewById(R.id.appview_rating_bar_rating_number1);
		}

		public void bindView(GetApp getApp) {

			try {
				GetAppMeta.Stats stats = getApp.getNodes().getMeta().getData().getStats();

				for (final GetAppMeta.Stats.Rating.Vote vote : stats.getRating().getVotes()) {
					bindViewForBar(vote.getValue(), 0, vote.getCount());
				}
			} catch (Exception e) {
				Logger.e(TAG, e);
			}
		}

		/**
		 * @param bar
		 * @param starState use 0 for an empty star, 1 for a half full star and 2 for a full star
		 * @param rating
		 */
		private void bindViewForBar(int bar, int starState, int rating) {
			int starDrawableResource = 0;
			switch (starState) {
				case 0:
					starDrawableResource = R.drawable.grid_item_star_empty_small;
					break;
				case 1:
					starDrawableResource = R.drawable.grid_item_star_half_small;
					break;
				case 2:
					starDrawableResource = R.drawable.grid_item_star_full_small;
					break;
			}

			String ratingStr = String.format(Locale.getDefault(), "%d", rating);

			switch (bar) {
				case 1:
					appview_rating_bar_star1.setImageResource(starDrawableResource);
					appview_rating_bar1.setProgress(rating);
					appview_rating_bar_rating_number1.setText(ratingStr);
					break;

				case 2:
					appview_rating_bar_star2.setImageResource(starDrawableResource);
					appview_rating_bar2.setProgress(rating);
					appview_rating_bar_rating_number2.setText(ratingStr);
					break;

				case 3:
					appview_rating_bar_star3.setImageResource(starDrawableResource);
					appview_rating_bar3.setProgress(rating);
					appview_rating_bar_rating_number3.setText(ratingStr);
					break;

				case 4:
					appview_rating_bar_star4.setImageResource(starDrawableResource);
					appview_rating_bar4.setProgress(rating);
					appview_rating_bar_rating_number4.setText(ratingStr);
					break;

				case 5:
					appview_rating_bar_star5.setImageResource(starDrawableResource);
					appview_rating_bar5.setProgress(rating);
					appview_rating_bar_rating_number5.setText(ratingStr);
					break;

				default:
					// do nothing
					break;
			}
		}
	}
}

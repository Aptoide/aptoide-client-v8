/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 09/08/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.model.v7.FullReview;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.implementations.RateAndReviewsFragment;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.RowReviewDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.BaseWidget;

/**
 * created by SithEngineer
 */
public class RowReviewWidget extends BaseWidget<RowReviewDisplayable> {

	//private final EnumStoreTheme theme;
	//private boolean isCommunity;
	public ImageView appIcon;
	public TextView rating;
	public TextView appName;
	public ImageView avatar;
	public TextView reviewer;
	public TextView reviewBody;
	public FrameLayout score;
	
	public RowReviewWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		appIcon = (ImageView )itemView.findViewById(R.id.app_icon);
		rating = (TextView )itemView.findViewById(R.id.rating);
		appName = (TextView )itemView.findViewById(R.id.app_name);
		avatar = (ImageView )itemView.findViewById(R.id.avatar);
		reviewer = (TextView )itemView.findViewById(R.id.reviewer);
		reviewBody = (TextView )itemView.findViewById(R.id.description);
		score = (FrameLayout )itemView.findViewById(R.id.score);
	}

	@Override
	public void bindView(RowReviewDisplayable displayable) {

		FullReview review = displayable.getPojo();
		GetAppMeta.App app = review.getData().getApp();

		if (app != null) {
			appName.setText(app.getName());
			ImageLoader.load(app.getIcon(), appIcon);
		} else {
			appName.setVisibility(View.INVISIBLE);
			appIcon.setVisibility(View.INVISIBLE);
		}

		reviewBody.setText(review.getBody());
		reviewer.setText(AptoideUtils.StringU.getFormattedString(R.string.reviewed_by, review.getUser().getName()));

		//rating.setText(AptoideUtils.StringUtils.getRoundedValueFromDouble(appItem.rating));
		rating.setText(String.format(Locale.getDefault(), "%d", (long)review.getStats().getRating()));
		ImageLoader.load(review.getUser().getAvatar(), avatar);

		//        ReviewViewHolder holder = (ReviewViewHolder) viewHolder;
//		final ReviewRowItem appItem = (ReviewRowItem) displayable;
//		final Context context = itemView.getContext();
//
//		if(theme != null) {
//			@ColorInt
//			int color = context.getResources().getColor(theme.getStoreHeader());
//			score.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
//		}

		itemView.setOnClickListener(v -> {
			((FragmentShower) getContext()).pushFragmentV4(RateAndReviewsFragment.newInstance(app.getId(), app.getName(), app.getStore()
					.getName(), app.getPackageName(), review.getId()));
		});

	}
}

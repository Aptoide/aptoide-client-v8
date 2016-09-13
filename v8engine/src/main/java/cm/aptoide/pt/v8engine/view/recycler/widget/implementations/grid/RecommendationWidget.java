/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 08/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;

import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.v8engine.BuildConfig;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.fragment.implementations.AppViewFragment;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.RecommendationDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by marcelobenites on 7/8/16.
 */
public class RecommendationWidget extends Widget<RecommendationDisplayable> {

	private TextView title;
	private TextView subtitle;
	private ImageView image;
	private ImageView appIcon;
	private TextView appName;
	private TextView similarApps;
	private TextView getApp;
	private CardView cardView;
	private RelativeLayout cardContent;

	public RecommendationWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		title = (TextView)itemView.findViewById(R.id.displayable_social_timeline_recommendation_card_title);
		subtitle = (TextView)itemView.findViewById(R.id.displayable_social_timeline_recommendation_card_subtitle);
		image = (ImageView) itemView.findViewById(R.id.displayable_social_timeline_recommendation_card_icon);
		appIcon = (ImageView) itemView.findViewById(R.id.displayable_social_timeline_recommendation_icon);
		appName = (TextView) itemView.findViewById(R.id.displayable_social_timeline_recommendation_name);
		similarApps = (TextView) itemView.findViewById(R.id.displayable_social_timeline_recommendation_similar_apps);
		getApp = (TextView) itemView.findViewById(R.id.displayable_social_timeline_recommendation_get_app_button);
		cardView = (CardView) itemView.findViewById(R.id.displayable_social_timeline_recommendation_card);
		cardContent = (RelativeLayout) itemView.findViewById(R.id.displayable_social_timeline_recommendation_card_content);
	}

	@Override
	public void bindView(RecommendationDisplayable displayable) {

		title.setText(displayable.getStyledTitle(getContext()));
		subtitle.setText(displayable.getTimeSinceRecommendation(getContext()));

		setCardviewMargin(displayable);

		ImageLoader.loadWithShadowCircleTransform(displayable.getAvatarResource(), image);

		ImageLoader.load(displayable.getAppIcon(), appIcon);

		appName.setText(displayable.getAppName());

		similarApps.setText(displayable.getSimilarAppsText(getContext()));

		getApp.setVisibility(View.VISIBLE);
		getApp.setText(displayable.getAppText(getContext()));
		cardContent.setOnClickListener(view -> {
			knockWithSixpackCredentials(displayable.getAbUrl());

			Analytics.AppsTimeline.clickOnCard("Recommendation", displayable.getPackageName(), Analytics.AppsTimeline.BLANK, displayable.getTitle(getContext
					()), Analytics.AppsTimeline.OPEN_APP_VIEW);
			((FragmentShower) getContext()).pushFragmentV4(AppViewFragment.newInstance(displayable.getAppId()));
		});
	}

	//// TODO: 31/08/16 refactor this out of here
	private void knockWithSixpackCredentials(String url) {
		if (url == null) {
			return;
		}

		String credential = Credentials.basic(BuildConfig.SIXPACK_USER, BuildConfig.SIXPACK_PASSWORD);

		OkHttpClient client = new OkHttpClient();

		Request click = new Request
				.Builder()
				.url(url)
				.addHeader("authorization", credential)
				.build();

		client.newCall(click).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				Logger.d(this.getClass().getSimpleName(), "sixpack request fail " + call.toString());
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				Logger.d(this.getClass().getSimpleName(), "knock success");
				response.body().close();
			}
		});
	}

	private void setCardviewMargin(RecommendationDisplayable displayable) {
		CardView.LayoutParams layoutParams = new CardView.LayoutParams(
				CardView.LayoutParams.WRAP_CONTENT, CardView.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(displayable.getMarginWidth(getContext(), getContext().getResources().getConfiguration().orientation),0,displayable
				.getMarginWidth
				(getContext(), getContext().getResources().getConfiguration().orientation),30);
		cardView.setLayoutParams(layoutParams);
	}

	@Override
	public void onViewAttached() {

	}

	@Override
	public void onViewDetached() {

	}
}

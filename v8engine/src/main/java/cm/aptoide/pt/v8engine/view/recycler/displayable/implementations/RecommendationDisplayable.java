/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 08/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.timeline.Recommendation;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.DateCalculator;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by marcelobenites on 7/8/16.
 */
@AllArgsConstructor
public class RecommendationDisplayable extends Displayable {

	@Getter private int avatarResource;
	@Getter private int titleResource;
	@Getter private long appId;
	@Getter private String appName;
	@Getter private String appIcon;

	private List<String> similarAppsNames;
	private Date date;
	private DateCalculator dateCalculator;
	private SpannableFactory spannableFactory;

	public static Displayable from(Recommendation recommendation, DateCalculator dateCalculator, SpannableFactory spannableFactory) {
		final List<String> similarAppsNames = new ArrayList<>();
		for (App similarApp: recommendation.getSimilarApps()) {
			similarAppsNames.add(similarApp.getName());
		}
		return new RecommendationDisplayable(R.mipmap.ic_launcher, R.string.displayable_social_timeline_recommendation_atptoide_team_recommends, recommendation
				.getRecommendedApp()
				.getId(),
				recommendation.getRecommendedApp().getName(), recommendation.getRecommendedApp().getIcon(), similarAppsNames, new Date(), dateCalculator,
				spannableFactory);
	}

	public RecommendationDisplayable() {
	}

	public String getTitle(Context context) {
		return context.getString(titleResource);
	}

	public Spannable getSimilarAppsText(Context context) {
		StringBuilder similarAppsText = new StringBuilder(context.getString(R.string.displayable_social_timeline_recommendation_similar_to, similarAppsNames.get(0)));
		for (int i = 1; i < similarAppsNames.size() - 1; i++) {
			similarAppsText.append(", ");
			similarAppsText.append(similarAppsNames.get(i));
		}
		if (similarAppsNames.size() > 1) {
			similarAppsText.append(" ");
			similarAppsText.append(context.getString(R.string.displayable_social_timeline_recommendation_similar_and));
			similarAppsText.append(" ");
			similarAppsText.append(similarAppsNames.get(similarAppsNames.size() - 1));
		}
		return spannableFactory.createStyleSpan(similarAppsText.toString(), Typeface.BOLD, similarAppsNames.toArray(new String[similarAppsNames.size()]));
	}

	public Spannable getAppText(Context context) {
		return spannableFactory.createStyleSpan(context
				.getString(R.string.displayable_social_timeline_article_get_app_button, appName), Typeface.BOLD, appName);
	}

	public String getHoursSinceLastUpdate(Context context) {
		return dateCalculator.getTimeSinceDate(context, date);
	}

	@Override
	public Type getType() {
		return Type.SOCIAL_TIMELINE;
	}

	@Override
	public int getViewLayout() {
		return R.layout.displayable_social_timeline_recommendation;
	}
}

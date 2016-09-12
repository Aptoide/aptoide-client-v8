/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 08/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.timeline.Recommendation;
import cm.aptoide.pt.utils.AptoideUtils;
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
	@Getter private String packageName;
	@Getter private String appName;
	@Getter private String appIcon;
	@Getter private String abUrl;

	private List<String> similarAppsNames;
	private Date date;
	private Date timestamp;
	private DateCalculator dateCalculator;
	private SpannableFactory spannableFactory;

	public static Displayable from(Recommendation recommendation, DateCalculator dateCalculator, SpannableFactory spannableFactory) {
		final List<String> similarAppsNames = new ArrayList<>();
		for (App similarApp: recommendation.getSimilarApps()) {
			similarAppsNames.add(similarApp.getName());
		}
		return new RecommendationDisplayable(R.mipmap.ic_launcher, R.string.displayable_social_timeline_recommendation_atptoide_team_recommends, recommendation.getRecommendedApp().getId(), recommendation
				.getRecommendedApp().getPackageName(),
				recommendation.getRecommendedApp().getName(), recommendation.getRecommendedApp().getIcon(),recommendation.getAb().getConversion().getUrl(),
				similarAppsNames,
				recommendation.getRecommendedApp().getUpdated(), recommendation.getTimestamp(),dateCalculator, spannableFactory);
	}

	public RecommendationDisplayable() {
	}

	public String getTitle(Context context) {
		return context.getString(titleResource);
	}

	public Spannable getStyledTitle(Context context) {
		String aptoide = "Aptoide";
		return spannableFactory.createColorSpan(context.getString(titleResource), ContextCompat.getColor(context, R.color.appstimeline_recommends_title), aptoide);
	}

	public int getMarginWidth(Context context, int orientation){
		if (!context.getResources().getBoolean(R.bool.is_this_a_tablet_device)) {
			return 0;
		}

		int width = AptoideUtils.ScreenU.getCachedDisplayWidth(orientation);

		if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
			return (int)(width * 0.2);
		} else {
			return (int)(width * 0.1);
		}
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
		return spannableFactory.createColorSpan(context.getString(R.string.displayable_social_timeline_article_get_app_button, ""), ContextCompat.getColor
				(context, R.color.appstimeline_grey), "");
	}

	public String getTimeSinceLastUpdate(Context context) {
		return dateCalculator.getTimeSinceDate(context, date);
	}

	public String getTimeSinceRecommendation(Context context) {
		return dateCalculator.getTimeSinceDate(context,timestamp);
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

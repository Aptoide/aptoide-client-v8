package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.text.Spannable;

import java.util.Date;

import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.model.v7.timeline.Feature;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by marcelobenites on 6/17/16.
 */
@AllArgsConstructor
public class FeatureDisplayable extends Displayable {

	@Getter private int avatarResource;
	@Getter private int titleResource;
	@Getter private String thumbnailUrl;
	@Getter private String url;
	@Getter private long appId;

	private String appName;
	private String title;
	private Date date;
	private DateCalculator dateCalculator;
	private SpannableFactory spannableFactory;

	public static FeatureDisplayable from(Feature feature, DateCalculator dateCalculator, SpannableFactory
			spannableFactory) {
		String appName = null;
		long appId = 0;
		if (feature.getApps() != null && feature.getApps().size() > 0) {
			appName = feature.getApps().get(0).getName();
			appId = feature.getApps().get(0).getId();
		}
		return new FeatureDisplayable(R.mipmap.ic_launcher, R.string.fragment_social_timeline_aptoide_team, feature.getThumbnailUrl(), feature.getUrl(),
				appId, appName, feature.getTitle(), feature.getDate(), dateCalculator, spannableFactory);
	}

	public FeatureDisplayable() {
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

	public String getTitle(Context context) {
		return context.getString(titleResource);
	}

	public String getTimeSinceLastUpdate(Context context) {
		return dateCalculator.getTimeSinceDate(context, date);
	}

	public boolean isGetApp() {
		return appName != null && appId != 0;
	}

	public Spannable getAppText(Context context) {
		return spannableFactory.createStyleSpan(context
				.getString(R.string.displayable_social_timeline_article_get_app_button, appName), Typeface.BOLD, appName);
	}

	@Override
	public Type getType() {
		return Type.SOCIAL_TIMELINE;
	}

	@Override
	public int getViewLayout() {
		return R.layout.displayable_social_timeline_feature;
	}
}

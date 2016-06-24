package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import java.util.Date;

import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.model.v7.timeline.Feature;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
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

	private String title;
	private Date date;
	private DateCalculator dateCalculator;

	public static FeatureDisplayable from(Feature feature, DateCalculator dateCalculator) {
		return new FeatureDisplayable(R.mipmap.ic_launcher, R.string.fragment_social_timeline_aptoide_team,
				feature.getThumbnailUrl(), feature.getUrl(), feature.getTitle(), feature.getDate(), dateCalculator);
	}

	public FeatureDisplayable() {
	}

	public String getTitle(Context context) {
		return context.getString(titleResource);
	}

	public String getHoursSinceLastUpdate(Context context) {
		return context.getString(R.string.fragment_social_timeline_hours_since_last_update, dateCalculator
				.getHoursSinceDate(date));
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

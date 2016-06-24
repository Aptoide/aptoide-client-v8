package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentActivity;

import java.util.Date;

import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.model.v7.timeline.Feature;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;

/**
 * Created by marcelobenites on 6/17/16.
 */
public class FeatureDisplayable extends Displayable {

	private DateCalculator dateCalculator;
	private Feature feature;

	public FeatureDisplayable() {
	}

	public FeatureDisplayable(Feature feature, DateCalculator dateCalculator) {
		this.dateCalculator = dateCalculator;
		this.feature = feature;
	}

	public String getTitle() {
		return feature.getTitle();
	}

	public String getUrl() {
		return feature.getUrl();
	}

	public String getAptoideText(Context context) {
		return context.getString(R.string.fragment_social_timeline_aptoide_team);
	}

	@DrawableRes public int getAvatart() {
		return R.mipmap.ic_launcher;
	}

	public String getThumbnailUrl() {
		return feature.getThumbnailUrl();
	}


	public String getHoursSinceLastUpdate(Context context) {
		return context.getString(R.string.fragment_social_timeline_hours_since_last_update, dateCalculator
				.getHoursSinceDate(feature.getDate()));
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

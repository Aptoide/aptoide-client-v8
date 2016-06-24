package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;

/**
 * Created by marcelobenites on 6/17/16.
 */
public class AppUpdateDisplayable extends Displayable {

	private App app;
	private SpannableFactory spannableFactory;

	public AppUpdateDisplayable() {
	}

	public AppUpdateDisplayable(App app, SpannableFactory spannableFactory) {
		this.app = app;
		this.spannableFactory = spannableFactory;
	}

	public Spannable getAppTitle(Context context) {
		return spannableFactory.create(context.getString(R.string.displayable_social_timeline_app_update_name,
				app.getName()), app.getName(), new StyleSpan(Typeface.BOLD));
	}

	public Spannable getHasUpdateText(Context context) {
		final String update = context.getString(R.string.displayable_social_timeline_app_update);
		return spannableFactory.create(context.getString(R.string.displayable_social_timeline_app_has_update, update),
				update, new ForegroundColorSpan(ContextCompat.getColor(context, R.color.aptoide_orange)));
	}

	public Spannable getVersionText(Context context) {
		return spannableFactory.create(context.getString(R.string.displayable_social_timeline_app_update_version,
				app.getFile().getVername()), app.getFile().getVername(), new StyleSpan(Typeface.BOLD));
	}

	public Spannable updateAppText(Context context) {
		String application = context.getString(R.string.displayable_social_timeline_app_update_application);
		return spannableFactory.create(context.getString(R.string.displayable_social_timeline_app_update_button,
				application), application, new StyleSpan(Typeface.BOLD));
	}

	public String getIconUrl() {
		return app.getIcon();
	}

	public long getAppId() {
		return app.getId();
	}

	@Override
	public Type getType() {
		return Type.SOCIAL_TIMELINE;
	}

	@Override
	public int getViewLayout() {
		return R.layout.displayable_social_timeline_app_update;
	}
}

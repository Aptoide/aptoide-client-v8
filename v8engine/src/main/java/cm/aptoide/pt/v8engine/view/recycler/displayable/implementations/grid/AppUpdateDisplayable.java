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
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by marcelobenites on 6/17/16.
 */
@AllArgsConstructor
public class AppUpdateDisplayable extends Displayable {

	@Getter private long appId;
	@Getter private String appIconUrl;

	private String appVersioName;
	private SpannableFactory spannableFactory;
	private String appName;

	public static AppUpdateDisplayable fromApp(App app, SpannableFactory spannableFactory) {
		return new AppUpdateDisplayable(app.getId(), app.getIcon(), app.getFile().getVername(), spannableFactory,
				app.getName());
	}

	public AppUpdateDisplayable() {
	}

	public Spannable getAppTitle(Context context) {
		return spannableFactory.create(context.getString(R.string.displayable_social_timeline_app_update_name,
				appName), appName, new StyleSpan(Typeface.BOLD));
	}

	public Spannable getHasUpdateText(Context context) {
		final String update = context.getString(R.string.displayable_social_timeline_app_update);
		return spannableFactory.create(context.getString(R.string.displayable_social_timeline_app_has_update, update),
				update, new ForegroundColorSpan(ContextCompat.getColor(context, R.color.aptoide_orange)));
	}

	public Spannable getVersionText(Context context) {
		return spannableFactory.create(context.getString(R.string.displayable_social_timeline_app_update_version,
				appVersioName), appVersioName, new StyleSpan(Typeface.BOLD));
	}

	public Spannable updateAppText(Context context) {
		String application = context.getString(R.string.displayable_social_timeline_app_update_application);
		return spannableFactory.create(context.getString(R.string.displayable_social_timeline_app_update_button,
				application), application, new StyleSpan(Typeface.BOLD));
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

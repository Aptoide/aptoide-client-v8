package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;

/**
 * Created by marcelobenites on 6/17/16.
 */
public class AppUpdateDisplayable extends Displayable {

	private DateCalculator dateCalculator;
	private App app;

	public AppUpdateDisplayable() {
	}

	public AppUpdateDisplayable(App app, DateCalculator dateCalculator) {
		this.app = app;
		this.dateCalculator = dateCalculator;
	}

	public String getAppName() {
		return app.getName();
	}

	public String getVersion() {
		return app.getFile().getVername();
	}

	public String getIconUrl() {
		return app.getIcon();
	}

	public long getAppId() {
		return app.getId();
	}

	public int getHoursSinceLastUpdate() {
		return dateCalculator.getHoursSinceDate(app.getUpdated());
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

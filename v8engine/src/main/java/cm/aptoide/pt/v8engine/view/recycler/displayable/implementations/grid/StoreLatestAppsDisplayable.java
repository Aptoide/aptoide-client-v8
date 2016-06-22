package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import java.util.ArrayList;
import java.util.List;

import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.timeline.StoreLatestApps;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import lombok.Data;

/**
 * Created by marcelobenites on 6/17/16.
 */
public class StoreLatestAppsDisplayable extends Displayable {

	private DateCalculator dateCalculator;
	private StoreLatestApps storeLatestApps;
	private List<LatestApp> latestApps;

	public StoreLatestAppsDisplayable() {
	}

	public StoreLatestAppsDisplayable(StoreLatestApps storeLatestApps, DateCalculator dateCalculator) {
		this.dateCalculator = dateCalculator;
		this.storeLatestApps = storeLatestApps;
		this.latestApps = new ArrayList<>(storeLatestApps.getApps().size());
	}

	public String getTitle() {
		return storeLatestApps.getStore().getName();
	}

	public List<LatestApp> getStoreLatestApps() {
		if (latestApps.isEmpty()) {
			for (App app : storeLatestApps.getApps()) {
				latestApps.add(new LatestApp(app.getId(), app.getIcon()));
			}
		}
		return latestApps;
	}

	public String getStoreName() {
		return storeLatestApps.getStore().getName();
	}

	public String getAvatartUrl() {
		return storeLatestApps.getStore().getAvatar();
	}

	public int getHoursSinceLastUpdate() {
		return 0;
	}

	@Override
	public Type getType() {
		return Type.SOCIAL_TIMELINE;
	}

	@Override
	public int getViewLayout() {
		return R.layout.displayable_social_timeline_latest_apps;
	}

	@Data
	public static class LatestApp {

		private final long appId;
		private final String iconUrl;

		public LatestApp(long appId, String iconUrl) {
			this.appId = appId;
			this.iconUrl = iconUrl;
		}
	}
}

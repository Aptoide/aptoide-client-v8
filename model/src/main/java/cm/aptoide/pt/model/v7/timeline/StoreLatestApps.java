package cm.aptoide.pt.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.store.Store;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class StoreLatestApps {

	@Getter private Store store;
	@Getter private List<App> apps;
	private Date latestUpdate;

	@JsonCreator public StoreLatestApps(@JsonProperty("store") Store store, @JsonProperty("apps") List<App> apps) {
		this.store = store;
		this.apps = apps;
	}

	public Date getLatestUpdate() {
		if (latestUpdate == null) {
			for (App app : apps) {
				if (latestUpdate == null || app.getUpdated().getTime() > latestUpdate.getTime()) {
					latestUpdate = app.getUpdated();
				}
			}
		}
		return latestUpdate;
	}
}

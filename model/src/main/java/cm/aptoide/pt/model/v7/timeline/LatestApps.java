package cm.aptoide.pt.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.store.Store;
import lombok.Data;

@Data
public class LatestApps {

	private Store store;
	private List<App> apps;

	@JsonCreator public LatestApps(@JsonProperty("store") Store store, @JsonProperty("apps") List<App> apps) {
		this.store = store;
		this.apps = apps;
	}
}

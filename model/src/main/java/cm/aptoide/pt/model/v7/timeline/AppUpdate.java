package cm.aptoide.pt.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import cm.aptoide.pt.model.v7.listapp.App;
import lombok.Data;

/**
 * Created by marcelobenites on 6/20/16.
 */
@Data
public class AppUpdate {

	private final List<App> apps;

	@JsonCreator
	public AppUpdate(@JsonProperty("apps") List<App> apps) {
		this.apps = apps;
	}
}

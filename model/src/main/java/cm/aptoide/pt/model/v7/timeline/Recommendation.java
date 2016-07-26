/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 08/07/2016.
 */

package cm.aptoide.pt.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

import cm.aptoide.pt.model.v7.listapp.App;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Created by marcelobenites on 7/8/16.
 */
@EqualsAndHashCode(exclude = {"recommendedApp", "similarApps"})
public class Recommendation implements TimelineCard {

	@Getter private final String cardId;
	@Getter private final App recommendedApp;
	@Getter private final List<App> similarApps;

	@JsonCreator public Recommendation(@JsonProperty("uid") String cardId, @JsonProperty("app") App recommendedApp, @JsonProperty("apps") List<App>
			similarApps) {
		this.cardId = cardId;
		this.recommendedApp = recommendedApp;
		this.similarApps = similarApps;
	}
}
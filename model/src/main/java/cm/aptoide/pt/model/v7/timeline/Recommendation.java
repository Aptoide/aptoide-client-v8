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
	@Getter @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC") private Date timestamp;
	@Getter private final App recommendedApp;
	@Getter private final List<App> similarApps;
	@Getter private final Ab ab;

	@JsonCreator public Recommendation(@JsonProperty("uid") String cardId,
	                                   @JsonProperty("timestamp") Date timestamp,
	                                   @JsonProperty("app") App recommendedApp,
	                                   @JsonProperty("apps")List<App>similarApps, @JsonProperty("ab") Ab ab) {
		this.ab = ab;
		this.cardId = cardId;
		this.timestamp=timestamp;
		this.recommendedApp = recommendedApp;
		this.similarApps = similarApps;
	}
}
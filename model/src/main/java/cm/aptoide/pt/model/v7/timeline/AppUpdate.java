/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 01/07/2016.
 */

package cm.aptoide.pt.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import cm.aptoide.pt.model.v7.listapp.App;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Created by marcelobenites on 7/1/16.
 */
@EqualsAndHashCode(callSuper = true)
public class AppUpdate extends App implements TimelineCard {

	@Getter private final String cardId;

	@JsonCreator public AppUpdate(@JsonProperty("uid") String cardId) {
		this.cardId = cardId;
	}
}
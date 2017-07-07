/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 01/07/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7.timeline;

import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Created by marcelobenites on 7/1/16.
 */
@EqualsAndHashCode(callSuper = false) public class AppUpdate extends App implements TimelineCard {

  @Getter private final String cardId;
  @Getter private final Ab ab;

  @JsonCreator public AppUpdate(@JsonProperty("uid") String cardId, @JsonProperty("ab") Ab ab) {
    this.cardId = cardId;
    this.ab = ab;
  }
}
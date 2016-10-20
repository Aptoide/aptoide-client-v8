/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 20/10/2016.
 */

package cm.aptoide.pt.v8engine.payment.providers.boacompra;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by marcelobenites on 20/10/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true) public class Sender {

  @JsonProperty("email") private final String email;

  @JsonCreator public Sender(@JsonProperty("email") String email) {
    this.email = email;
  }

  public String getEmail() {
    return email;
  }
}

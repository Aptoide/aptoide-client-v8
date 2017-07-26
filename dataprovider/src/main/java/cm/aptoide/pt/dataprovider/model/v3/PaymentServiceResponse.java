/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 27/07/2016.
 */

package cm.aptoide.pt.dataprovider.model.v3;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by rmateus on 21-05-2014.
 */
@Data public class PaymentServiceResponse {

  @JsonProperty("id") private int id;

  @JsonProperty("short_name") private String shortName;

  @JsonProperty("name") private String name;

  @JsonProperty("description") private String description;

  @JsonProperty("price") private double price;

  @JsonProperty("currency") private String currency;

  @JsonProperty("tax_rate") private double taxRate;

  @JsonProperty("sign") private String sign;

  @JsonProperty("needsauth") private boolean authorizationRequired;
}
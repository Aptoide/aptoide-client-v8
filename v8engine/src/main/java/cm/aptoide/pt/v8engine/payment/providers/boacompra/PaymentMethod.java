/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 20/10/2016.
 */

package cm.aptoide.pt.v8engine.payment.providers.boacompra;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by marcelobenites on 20/10/16.
 */
public class PaymentMethod {

  private final String name;
  private final String brand;
  private final String creditCardDescription;

  @JsonCreator public PaymentMethod(@JsonProperty("payment-method") String name,
      @JsonProperty("brand") String brand,
      @JsonProperty("credit-card") String creditCardDescription) {
    this.name = name;
    this.brand = brand;
    this.creditCardDescription = creditCardDescription;
  }

  public String getName() {
    return name;
  }

  public String getBrand() {
    return brand;
  }

  public String getCreditCardDescription() {
    return creditCardDescription;
  }
}

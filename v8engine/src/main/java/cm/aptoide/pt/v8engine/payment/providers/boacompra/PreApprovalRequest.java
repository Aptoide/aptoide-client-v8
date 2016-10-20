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

public class PreApprovalRequest {

  @JsonProperty("reference") private final String reference;
  @JsonProperty("notify-url") private final String notifyUrl;
  @JsonProperty("store-id") private final int storeId;
  @JsonProperty("return-url") private final String returnUrl;
  @JsonProperty("description") private final String description;
  @JsonProperty("payment-group") private final String paymentGroup;
  @JsonProperty("sender") private final Sender sender;

  public PreApprovalRequest(String reference, int storeId, String notifyUrl, String returnUrl,
      String description, String paymentGroup, Sender sender) {
    this.reference = reference;
    this.storeId = storeId;
    this.notifyUrl = notifyUrl;
    this.returnUrl = returnUrl;
    this.description = description;
    this.paymentGroup = paymentGroup;
    this.sender = sender;
  }

  public String getReference() {
    return reference;
  }

  public int getStoreId() {
    return storeId;
  }

  public String getNotifyUrl() {
    return notifyUrl;
  }

  public String getReturnUrl() {
    return returnUrl;
  }

  public String getDescription() {
    return description;
  }

  public String getPaymentGroup() {
    return paymentGroup;
  }

  public Sender getSender() {
    return sender;
  }

}

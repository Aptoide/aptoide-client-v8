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
@JsonIgnoreProperties(ignoreUnknown = true) public class PreApproval {

  private final String code;
  private final String reference;
  private final String status;
  private final String notifyUrl;
  private final PaymentMethod paymentMethod;
  private final Sender sender;

  @JsonCreator public PreApproval(@JsonProperty("pre-approval-code") String code,
      @JsonProperty("reference") String reference, @JsonProperty("status") String status,
      @JsonProperty("notify-url") String notifyUrl,
      @JsonProperty("payment-method-information") PaymentMethod paymentMethod, @JsonProperty("sender")Sender sender) {
    this.code = code;
    this.reference = reference;
    this.status = status;
    this.notifyUrl = notifyUrl;
    this.paymentMethod = paymentMethod;
    this.sender = sender;
  }

  public String getCode() {
    return code;
  }

  public String getReference() {
    return reference;
  }

  public String getStatus() {
    return status;
  }

  public String getNotifyUrl() {
    return notifyUrl;
  }

  public PaymentMethod getPaymentMethod() {
    return paymentMethod;
  }

  public Sender getSender() {
    return sender;
  }
}
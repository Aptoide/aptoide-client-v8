/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 07/10/2016.
 */

package cm.aptoide.pt.v8engine.payment.providers.boacompra;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by marcelobenites on 10/7/16.
 */
class PreApprovalRequest {

  @JsonProperty("pre-approval") private final CompletePreApproval completePreApproval;

  @JsonCreator
  public PreApprovalRequest(PreApprovalRequest.CompletePreApproval completePreApproval) {
    this.completePreApproval = completePreApproval;
  }

  public CompletePreApproval getCompletePreApproval() {
    return completePreApproval;
  }

  public static class CompletePreApproval {

    @JsonProperty("reference") private final String reference;
    @JsonProperty("store-id") private final int storeId;
    @JsonProperty("notify-url") private final String notifyUrl;
    @JsonProperty("return-url") private final String returnUrl;
    @JsonProperty("description") private final String description;
    @JsonProperty("payment-group") private final String paymentGroup;
    @JsonProperty("sender") private final Sender sender;

    @JsonCreator
    public CompletePreApproval(String reference, int storeId, String notifyUrl, String returnUrl,
        String description, String paymentGroup,
        PreApprovalRequest.CompletePreApproval.Sender sender) {
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

    public static class Sender {

      @JsonProperty("email") private final String email;

      @JsonCreator public Sender(String email) {
        this.email = email;
      }

      public String getEmail() {
        return email;
      }
    }
  }
}

/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 15/11/2016.
 */

package cm.aptoide.pt.model.v3;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by marcelobenites on 15/11/16.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GetProductPurchaseAuthorizationResponse extends BaseV3Response {

  public enum Status {
    INITIATED, PENDING, ACTIVE, EXPIRED, CANCELLED,
    CANCELLED_BY_CHARGEBACK, REJECTED,
    PAYMENT_METHOD_CHANGE,
    PENDING_PAYMENT_METHOD
  }

  private String url;
  private String successUrl;
  private Status authorizationStatus;

  public boolean isAuthorized() {
    return Status.ACTIVE.equals(authorizationStatus);
  }
}
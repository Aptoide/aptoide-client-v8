/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 25/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import cm.aptoide.pt.iab.InAppBillingSerializer;
import cm.aptoide.pt.model.v3.InAppBillingPurchasesResponse;
import cm.aptoide.pt.model.v3.PaidApp;
import java.io.IOException;

/**
 * Created by marcelobenites on 8/25/16.
 */
public class PurchaseFactory {

  private final InAppBillingSerializer serializer;

  public PurchaseFactory(InAppBillingSerializer serializer) {
    this.serializer = serializer;
  }

  public Purchase create(InAppBillingPurchasesResponse.InAppBillingPurchase purchase,
      String purchaseSignature) {
    return new Purchase() {

      @Override public String getData() throws IOException {
        return serializer.serializePurchase(purchase);
      }

      @Override public String getSignature() {
        return purchaseSignature;
      }
    };
  }

  public Purchase create(PaidApp app) {
    return new Purchase() {

      @Override public String getData() {
        return app.getPath()
            .getStringPath();
      }

      @Override public String getSignature() {
        return null;
      }
    };
  }
}

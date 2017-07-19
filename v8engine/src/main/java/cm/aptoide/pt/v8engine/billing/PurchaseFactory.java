/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 25/08/2016.
 */

package cm.aptoide.pt.v8engine.billing;

import cm.aptoide.pt.dataprovider.model.v3.InAppBillingPurchasesResponse;
import cm.aptoide.pt.dataprovider.model.v3.PaidApp;
import cm.aptoide.pt.v8engine.billing.external.ExternalBillingSerializer;
import cm.aptoide.pt.v8engine.billing.product.InAppPurchase;
import cm.aptoide.pt.v8engine.billing.product.PaidAppPurchase;
import com.fasterxml.jackson.core.JsonProcessingException;

public class PurchaseFactory {

  private final ExternalBillingSerializer serializer;

  public PurchaseFactory(ExternalBillingSerializer serializer) {
    this.serializer = serializer;
  }

  public Purchase create(InAppBillingPurchasesResponse.InAppBillingPurchase purchase,
      String purchaseSignature, int apiVersion, String sku) {
    try {
      return new InAppPurchase(apiVersion, purchase.getPackageName(), purchase.getPurchaseToken(),
          purchaseSignature, serializer.serializePurchase(purchase), sku);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public Purchase create(PaidApp app) {
    return new PaidAppPurchase(app.getPath()
        .getStringPath());
  }
}

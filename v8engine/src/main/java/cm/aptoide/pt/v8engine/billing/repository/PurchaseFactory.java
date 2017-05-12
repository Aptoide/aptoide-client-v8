/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 25/08/2016.
 */

package cm.aptoide.pt.v8engine.billing.repository;

import cm.aptoide.pt.model.v3.InAppBillingPurchasesResponse;
import cm.aptoide.pt.model.v3.PaidApp;
import cm.aptoide.pt.v8engine.billing.Purchase;
import cm.aptoide.pt.v8engine.billing.inapp.InAppBillingSerializer;
import cm.aptoide.pt.v8engine.billing.purchase.InAppPurchase;
import cm.aptoide.pt.v8engine.billing.purchase.PaidAppPurchase;

public class PurchaseFactory {

  private final InAppBillingSerializer serializer;
  private final InAppBillingRepository repository;

  public PurchaseFactory(InAppBillingSerializer serializer, InAppBillingRepository repository) {
    this.serializer = serializer;
    this.repository = repository;
  }

  public Purchase create(InAppBillingPurchasesResponse.InAppBillingPurchase purchase,
      String purchaseSignature, int apiVersion) {
    return new InAppPurchase(purchase, purchaseSignature, serializer, repository, apiVersion);
  }

  public Purchase create(PaidApp app) {
    return new PaidAppPurchase(app.getPath().getStringPath());
  }
}

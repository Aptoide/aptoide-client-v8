/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 25/08/2016.
 */

package cm.aptoide.pt.billing.networking;

import cm.aptoide.pt.billing.BillingIdManager;
import cm.aptoide.pt.billing.external.ExternalBillingSerializer;
import cm.aptoide.pt.billing.purchase.Purchase;
import cm.aptoide.pt.billing.purchase.PurchaseFactory;
import cm.aptoide.pt.dataprovider.ws.v7.billing.PurchaseResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.ArrayList;
import java.util.List;

public class PurchaseMapperV7 {

  private final ExternalBillingSerializer serializer;
  private final BillingIdManager billingIdManager;
  private final PurchaseFactory purchaseFactory;

  public PurchaseMapperV7(ExternalBillingSerializer serializer, BillingIdManager billingIdManager,
      PurchaseFactory purchaseFactory) {
    this.serializer = serializer;
    this.billingIdManager = billingIdManager;
    this.purchaseFactory = purchaseFactory;
  }

  public List<Purchase> map(List<PurchaseResponse> responseList) {

    final List<Purchase> purchases = new ArrayList<>(responseList.size());

    for (PurchaseResponse response : responseList) {
      purchases.add(map(response));
    }
    return purchases;
  }

  public Purchase map(PurchaseResponse response) {
    try {
      return purchaseFactory.create(billingIdManager.generatePurchaseId(response.getProduct()
              .getId()), response.getSignature(), serializer.serializePurchase(response.getData()
              .getDeveloperPurchase()), response.getData()
              .getDeveloperPurchase()
              .getPurchaseState() == 0 ? Purchase.Status.COMPLETED : Purchase.Status.NEW,
          response.getProduct()
              .getSku(), PurchaseFactory.IN_APP, null, null);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException(e);
    }
  }
}

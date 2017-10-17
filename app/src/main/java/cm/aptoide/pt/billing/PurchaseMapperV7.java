/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 25/08/2016.
 */

package cm.aptoide.pt.billing;

import cm.aptoide.pt.billing.external.ExternalBillingSerializer;
import cm.aptoide.pt.billing.product.SimplePurchase;
import cm.aptoide.pt.dataprovider.ws.v7.billing.GetPurchasesRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.ArrayList;
import java.util.List;

public class PurchaseMapperV7 {

  private final ExternalBillingSerializer serializer;
  private final IdResolver idResolver;
  private final PurchaseFactory purchaseFactory;

  public PurchaseMapperV7(ExternalBillingSerializer serializer, IdResolver idResolver,
      PurchaseFactory purchaseFactory) {
    this.serializer = serializer;
    this.idResolver = idResolver;
    this.purchaseFactory = purchaseFactory;
  }

  public List<Purchase> map(List<GetPurchasesRequest.ResponseBody.Purchase> responseList) {

    final List<Purchase> purchases = new ArrayList<>(responseList.size());

    for (GetPurchasesRequest.ResponseBody.Purchase response : responseList) {
      purchases.add(map(response));
    }
    return purchases;
  }

  public Purchase map(GetPurchasesRequest.ResponseBody.Purchase response) {
    try {
      return purchaseFactory.create(idResolver.generatePurchaseId(response.getProduct()
              .getId()), response.getSignature(), serializer.serializePurchase(response.getData()
              .getDeveloperPurchase()), response.getData()
              .getDeveloperPurchase()
              .getPurchaseState() == 0 ? SimplePurchase.Status.COMPLETED : SimplePurchase.Status.NEW,
          response.getProduct()
              .getSku());
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException(e);
    }
  }
}

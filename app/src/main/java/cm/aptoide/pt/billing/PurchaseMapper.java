/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 25/08/2016.
 */

package cm.aptoide.pt.billing;

import cm.aptoide.pt.billing.external.ExternalBillingSerializer;
import cm.aptoide.pt.billing.product.InAppPurchase;
import cm.aptoide.pt.billing.product.PaidAppPurchase;
import cm.aptoide.pt.billing.product.SimplePurchase;
import cm.aptoide.pt.dataprovider.model.v3.PaidApp;
import cm.aptoide.pt.dataprovider.ws.v7.billing.GetPurchasesRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.ArrayList;
import java.util.List;

public class PurchaseMapper {

  private final ExternalBillingSerializer serializer;
  private final BillingIdResolver idResolver;

  public PurchaseMapper(ExternalBillingSerializer serializer, BillingIdResolver idResolver) {
    this.serializer = serializer;
    this.idResolver = idResolver;
  }

  public Purchase map(PaidApp response) {
    return new PaidAppPurchase(response.getPath()
        .getStringPath(), response.getPayment()
        .isPaid() ? SimplePurchase.Status.COMPLETED : SimplePurchase.Status.FAILED,
        idResolver.resolveProductId(response.getPath()
            .getAppId()));
  }

  public List<Purchase> map(List<GetPurchasesRequest.ResponseBody.Purchase> responseList) {

    final List<Purchase> purchases = new ArrayList<>(responseList.size());

    try {
      for (GetPurchasesRequest.ResponseBody.Purchase response : responseList) {
        purchases.add(new InAppPurchase(response.getSignature(), serializer.serializePurchase(
            response.getData()
                .getDeveloperData()), response.getProduct()
            .getSku(), response.getData()
            .getDeveloperData()
            .getPurchaseToken(), response.getData()
            .getDeveloperData()
            .getPurchaseState() == 0 ? SimplePurchase.Status.COMPLETED : SimplePurchase.Status.NEW,
            idResolver.resolveProductId(response.getProduct()
                .getId())));
      }
    } catch (JsonProcessingException ignored) {
    }

    return purchases;
  }
}

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
import java.util.ArrayList;
import java.util.List;

public class PurchaseMapper {

  private final ExternalBillingSerializer serializer;

  public PurchaseMapper(ExternalBillingSerializer serializer) {
    this.serializer = serializer;
  }

  public Purchase map(PaidApp response) {
    return new PaidAppPurchase(response.getPath()
        .getStringPath(), response.getPayment()
        .isPaid());
  }

  public Purchase map(InAppBillingPurchasesResponse response, String sku) {

    try {
      if (response != null
          && response.getPurchaseInformation() != null
          && response.getPurchaseInformation()
          .getPurchaseList() != null
          && response.getPurchaseInformation()
          .getSignatureList() != null
          && response.getPurchaseInformation()
          .getSkuList() != null) {

        String responseSku;
        InAppBillingPurchasesResponse.InAppBillingPurchase responsePurchase;
        for (int i = 0; i < response.getPurchaseInformation()
            .getPurchaseList()
            .size(); i++) {

          responseSku = response.getPurchaseInformation()
              .getSkuList()
              .get(i);
          responsePurchase = response.getPurchaseInformation()
              .getPurchaseList()
              .get(i);
          if (responseSku.equals(sku)) {
            return new InAppPurchase(response.getPurchaseInformation()
                .getSignatureList()
                .get(i), serializer.serializePurchase(responsePurchase), responseSku,
                responsePurchase.getPurchaseState() == 0, responsePurchase.getPurchaseToken());
          }
        }
      }
    } catch (JsonProcessingException ignored) {
    }

    throw new IllegalArgumentException("Could not map purchase");
  }

  public List<Purchase> map(InAppBillingPurchasesResponse response) {

    final List<Purchase> purchases = new ArrayList<>();

    try {
      if (response != null
          && response.getPurchaseInformation() != null
          && response.getPurchaseInformation()
          .getPurchaseList() != null
          && response.getPurchaseInformation()
          .getSignatureList() != null
          && response.getPurchaseInformation()
          .getSkuList() != null) {

        InAppBillingPurchasesResponse.InAppBillingPurchase responsePurchase;
        for (int i = 0; i < response.getPurchaseInformation()
            .getPurchaseList()
            .size(); i++) {

          responsePurchase = response.getPurchaseInformation()
              .getPurchaseList()
              .get(i);

          purchases.add(new InAppPurchase(response.getPurchaseInformation()
              .getSignatureList()
              .get(i), serializer.serializePurchase(response.getPurchaseInformation()
              .getPurchaseList()
              .get(i)), response.getPurchaseInformation()
              .getSkuList()
              .get(i), responsePurchase.getPurchaseState() == 0,
              responsePurchase.getPurchaseToken()));
        }
      }
    } catch (JsonProcessingException ignored) {
    }

    return purchases;
  }
}

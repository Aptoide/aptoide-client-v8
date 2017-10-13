/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 25/08/2016.
 */

package cm.aptoide.pt.billing;

import cm.aptoide.pt.billing.product.PaidAppPurchase;
import cm.aptoide.pt.billing.product.SimplePurchase;
import cm.aptoide.pt.dataprovider.model.v3.PaidApp;

public class PurchaseMapperV3 {

  public Purchase map(PaidApp response, String productId) {

    if (response.isOk() && response.isPaid()) {
      return new PaidAppPurchase(response.getPath()
          .getStringPath(), SimplePurchase.Status.COMPLETED, productId);
    } else {
      return new SimplePurchase(SimplePurchase.Status.FAILED, productId);
    }
  }
}

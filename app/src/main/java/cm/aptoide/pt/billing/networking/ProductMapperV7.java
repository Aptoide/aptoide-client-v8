/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/08/2016.
 */

package cm.aptoide.pt.billing.networking;

import cm.aptoide.pt.billing.BillingIdManager;
import cm.aptoide.pt.billing.Price;
import cm.aptoide.pt.billing.product.InAppProduct;
import cm.aptoide.pt.billing.product.Product;
import cm.aptoide.pt.dataprovider.ws.v7.billing.GetProductsRequest;
import java.util.ArrayList;
import java.util.List;

public class ProductMapperV7 {

  private final BillingIdManager billingIdManager;

  public ProductMapperV7(BillingIdManager billingIdManager) {
    this.billingIdManager = billingIdManager;
  }

  public List<Product> map(String packageName,
      List<GetProductsRequest.ResponseBody.Product> responseList, int packageVersionCode) {

    final List<Product> products = new ArrayList<>(responseList.size());

    for (GetProductsRequest.ResponseBody.Product response : responseList) {

      products.add(map(packageName, packageVersionCode, response));
    }

    return products;
  }

  public Product map(String packageName, int packageVersionCode,
      GetProductsRequest.ResponseBody.Product response) {
    return new InAppProduct(billingIdManager.generateProductId(response.getId()), response.getSku(),
        response.getIcon(), response.getTitle(), response.getDescription(), packageName, new Price(
        response.getPrice()
            .getAmount(), response.getPrice()
        .getCurrency(), response.getPrice()
        .getSign()), packageVersionCode);
  }
}

/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/08/2016.
 */

package cm.aptoide.pt.billing.product;

import cm.aptoide.pt.billing.IdResolver;
import cm.aptoide.pt.billing.Price;
import cm.aptoide.pt.billing.Product;
import cm.aptoide.pt.dataprovider.ws.v7.billing.GetProductsRequest;
import java.util.ArrayList;
import java.util.List;

public class ProductMapperV7 {

  private final IdResolver idResolver;

  public ProductMapperV7(IdResolver idResolver) {
    this.idResolver = idResolver;
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
    return new InAppProduct(idResolver.generateProductId(response.getId()), response.getSku(),
        response.getIcon(), response.getTitle(), response.getDescription(), packageName, new Price(
        response.getPrice()
            .getAmount(), response.getPrice()
        .getCurrency(), response.getPrice()
        .getSign()), packageVersionCode);
  }
}

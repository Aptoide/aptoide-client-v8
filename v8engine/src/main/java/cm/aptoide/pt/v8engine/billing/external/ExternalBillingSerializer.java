/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 24/08/2016.
 */

package cm.aptoide.pt.v8engine.billing.external;

import cm.aptoide.pt.dataprovider.model.v3.InAppBillingPurchasesResponse;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.product.InAppProduct;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ExternalBillingSerializer {

  public List<String> serializeProducts(List<Product> products) throws IOException {
    final List<String> serializedProducts = new ArrayList<>();
    for (Product product : products) {
      serializedProducts.add(new ObjectMapper().writeValueAsString(
          new SKU(((InAppProduct) product).getSku(), "inapp", getPrice(product), product.getPrice()
              .getCurrency(), (long) (product.getPrice()
              .getAmount() * 1000000), product.getTitle(), product.getDescription())));
    }
    return serializedProducts;
  }

  public String serializePurchase(InAppBillingPurchasesResponse.InAppBillingPurchase purchase)
      throws JsonProcessingException {
    return new ObjectMapper().writeValueAsString(purchase);
  }

  private String getPrice(Product product) {
    return String.format(Locale.US, "%s %.2f", product.getPrice()
        .getCurrencySymbol(), product.getPrice()
        .getAmount());
  }

  private static class SKU {

    private String productId;

    private String type;

    private String price;

    @JsonProperty("price_currency_code") private String currency;

    @JsonProperty("price_amount_micros") private long amount;

    private String title;

    private String description;

    public SKU(String productId, String type, String price, String currency, long amount,
        String title, String description) {
      this.productId = productId;
      this.type = type;
      this.price = price;
      this.currency = currency;
      this.amount = amount;
      this.title = title;
      this.description = description;
    }

    public String getProductId() {
      return productId;
    }

    public String getType() {
      return type;
    }

    public String getPrice() {
      return price;
    }

    public String getCurrency() {
      return currency;
    }

    public long getAmount() {
      return amount;
    }

    public String getTitle() {
      return title;
    }

    public String getDescription() {
      return description;
    }
  }
}
package cm.aptoide.pt.billing;

import cm.aptoide.pt.billing.order.Order;
import cm.aptoide.pt.billing.order.OrdersList;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by neuro on 05-11-2017.
 */

public class LocalOrdersParser {

  private final ObjectMapper objectMapper;

  public LocalOrdersParser(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public List<LocalOrdersManager.Order> parse(InputStream inputStream) throws IOException {
    OrdersList ordersList = objectMapper.readValue(inputStream, OrdersList.class);

    List<LocalOrdersManager.Order> orders = new LinkedList<>();

    for (Order order : ordersList.orderList) {
      String signature = order.signature;
      String signatureData = objectMapper.writeValueAsString(order.data.developerPurchase);
      String purchaseToken = order.data.developerPurchase.purchaseToken;
      String productId = order.data.developerPurchase.productId;
      String sku = order.product.sku;

      orders.add(
          new LocalOrdersManager.Order(signature, signatureData, purchaseToken, productId, sku));
    }

    return orders;
  }
}

package cm.aptoide.pt.billing.authorization;

import cm.aptoide.pt.billing.Price;

public class AuthorizationFactory {

  public static final String WEB = "WEB";
  public static final String PAYPAL_SDK = "PAYPAL_SDK";

  public Authorization create(long id, String customerId, String type, Authorization.Status status,
      String url, String redirectUrl, String metadata, Price price, String description,
      long transactionId) {
    switch (type) {
      case WEB:
        return new WebAuthorization(id, customerId, status, url, redirectUrl, transactionId);
      case PAYPAL_SDK:
        return new PayPalAuthorization(id, customerId, status, transactionId, metadata, price,
            description);
      default:
        return new Authorization(id, customerId, status, transactionId);
    }
  }
}
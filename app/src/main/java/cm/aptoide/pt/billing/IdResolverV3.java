package cm.aptoide.pt.billing;

public class IdResolverV3 implements IdResolver {

  @Override public String generateAuthorizationId(long authorizationId) {
    return "authorization" + authorizationId;
  }

  @Override public String generateTransactionId(long transactionId) {
    return "transaction" + transactionId;
  }

  @Override public String generateProductId(long productId) {
    return "product" + productId;
  }

  @Override public String generateServiceId(long serviceId) {
    return "service" + serviceId;
  }

  @Override public String generatePurchaseId(long purchaseId) {
    return "purchase" + purchaseId;
  }

  @Override public long resolveProductId(String productId) {
    return Long.valueOf(productId.replace("product", ""));
  }

  @Override public long resolveServiceId(String serviceId) {
    return Long.valueOf(serviceId.replace("service", ""));
  }

  @Override public long resolvePurchaseId(String purchaseId) {
    return Long.valueOf(purchaseId.replace("purchase", ""));
  }

  @Override public long resolveTransactionId(String transactionId) {
    return Long.valueOf(transactionId.replace("transaction", ""));
  }
}

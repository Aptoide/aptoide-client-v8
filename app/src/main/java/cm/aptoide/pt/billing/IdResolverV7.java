package cm.aptoide.pt.billing;

public class IdResolverV7 implements IdResolver {

  @Override public String generateAuthorizationId(long authorizationId) {
    return String.valueOf(authorizationId);
  }

  @Override public String generateTransactionId(long transactionId) {
    return String.valueOf(transactionId);
  }

  @Override public String generateProductId(long productId) {
    return String.valueOf(productId);
  }

  @Override public String generateServiceId(long serviceId) {
    return String.valueOf(serviceId);
  }

  @Override public String generatePurchaseId(long purchaseId) {
    return String.valueOf(purchaseId);
  }

  @Override public long resolveProductId(String productId) {
    return Long.valueOf(productId);
  }

  @Override public long resolveServiceId(String serviceId) {
    return Long.valueOf(serviceId);
  }

  @Override public long resolvePurchaseId(String purchaseId) {
    return Long.valueOf(purchaseId);
  }

  @Override public long resolveTransactionId(String transactionId) {
    return Long.valueOf(transactionId);
  }
}

package cm.aptoide.pt.billing;

public interface IdResolver {
  String generateAuthorizationId(long authorizationId);

  String generateTransactionId(long transactionId);

  String generateProductId(long productId);

  String generateServiceId(long serviceId);

  String generatePurchaseId(long purchaseId);

  long resolveProductId(String productId);

  long resolveServiceId(String serviceId);

  long resolvePurchaseId(String purchaseId);

  long resolveTransactionId(String transactionId);
}

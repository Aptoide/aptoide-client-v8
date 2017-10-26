package cm.aptoide.pt.billing;

public interface BillingIdManager {
  String generateAuthorizationId(long authorizationId);

  String generateTransactionId(long transactionId);

  String generateProductId(long productId);

  String generateServiceId(long serviceId);

  String generatePurchaseId(long purchaseId);

  long resolveProductId(String productId);

  long resolveServiceId(String serviceId);

  long resolvePurchaseId(String purchaseId);

  long resolveTransactionId(String transactionId);

  String generateServiceId();

  String generateTransactionId();

  String generateAuthorizationId();
}

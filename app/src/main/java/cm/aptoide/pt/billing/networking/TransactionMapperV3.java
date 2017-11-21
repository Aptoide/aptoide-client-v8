package cm.aptoide.pt.billing.networking;

import cm.aptoide.pt.billing.BillingIdManager;
import cm.aptoide.pt.billing.transaction.Transaction;
import cm.aptoide.pt.billing.transaction.TransactionFactory;
import cm.aptoide.pt.dataprovider.model.v3.ErrorResponse;
import cm.aptoide.pt.dataprovider.model.v3.TransactionResponse;
import java.util.List;

public class TransactionMapperV3 {

  private final TransactionFactory transactionFactory;
  private final BillingIdManager billingIdManager;

  public TransactionMapperV3(TransactionFactory transactionFactory,
      BillingIdManager billingIdManager) {
    this.transactionFactory = transactionFactory;
    this.billingIdManager = billingIdManager;
  }

  public Transaction map(String customerId, String transactionId,
      TransactionResponse transactionResponse, String productId) {

    if (transactionResponse.hasErrors()) {
      return getErrorTransaction(transactionResponse.getErrors(), customerId, transactionId,
          billingIdManager.generateServiceId(1), productId);
    }

    final Transaction.Status status;
    switch (transactionResponse.getTransactionStatus()) {
      case "COMPLETED":
        status = Transaction.Status.COMPLETED;
        break;
      case "PENDING_USER_AUTHORIZATION":
      case "CREATED":
        status = Transaction.Status.PENDING_SERVICE_AUTHORIZATION;
        break;
      case "PROCESSING":
      case "PENDING":
        status = Transaction.Status.PROCESSING;
        break;
      case "FAILED":
      case "CANCELED":
      default:
        status = Transaction.Status.FAILED;
    }

    return transactionFactory.create(transactionId, customerId, productId, status);
  }

  private Transaction getErrorTransaction(List<ErrorResponse> errors, String customerId,
      String transactionId, String serviceId, String productId) {

    Transaction transaction =
        transactionFactory.create(transactionId, customerId, productId, Transaction.Status.FAILED);

    if (errors == null || errors.isEmpty()) {
      return transaction;
    }

    final ErrorResponse error = errors.get(0);

    if ("PRODUCT-204".equals(error.code)
        || "PRODUCT-209".equals(error.code)
        || "PRODUCT-214".equals(error.code)) {
      transaction = transactionFactory.create(transactionId, customerId, productId,
          Transaction.Status.PENDING_SERVICE_AUTHORIZATION);
    }

    if ("PRODUCT-200".equals(error.code)) {
      transaction = transactionFactory.create(transactionId, customerId, productId,
          Transaction.Status.COMPLETED);
    }

    if ("PRODUCT-216".equals(error.code) || "SYS-1".equals(error.code)) {
      transaction = transactionFactory.create(transactionId, customerId, productId,
          Transaction.Status.PROCESSING);
    }

    return transaction;
  }
}

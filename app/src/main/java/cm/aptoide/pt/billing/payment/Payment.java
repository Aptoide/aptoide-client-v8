package cm.aptoide.pt.billing.payment;

import cm.aptoide.pt.billing.authorization.Authorization;
import cm.aptoide.pt.billing.product.Product;
import cm.aptoide.pt.billing.purchase.Purchase;
import cm.aptoide.pt.billing.transaction.AuthorizedTransaction;
import cm.aptoide.pt.billing.transaction.Transaction;
import java.util.List;

public class Payment {

  private final Product product;
  private final PaymentService selectedPaymentService;
  private final Transaction transaction;
  private final Purchase purchase;
  private final List<PaymentService> paymentServices;

  public Payment(Product product, PaymentService selectedPaymentService, Transaction transaction,
      Purchase purchase, List<PaymentService> paymentServices) {
    this.product = product;
    this.selectedPaymentService = selectedPaymentService;
    this.transaction = transaction;
    this.purchase = purchase;
    this.paymentServices = paymentServices;
  }

  public Product getProduct() {
    return product;
  }

  public PaymentService getSelectedPaymentService() {
    return selectedPaymentService;
  }

  public Purchase getPurchase() {
    return purchase;
  }

  public Transaction getTransaction() {
    return transaction;
  }

  public List<PaymentService> getPaymentServices() {
    return paymentServices;
  }

  public boolean isNew() {
    if (transaction.isNew() && !purchase.isCompleted()) {
      return true;
    }

    if (transaction.isCompleted() && !purchase.isCompleted()) {
      return true;
    }

    return false;
  }

  public Authorization getAuthorization() {
    if (transaction instanceof AuthorizedTransaction) {
      return ((AuthorizedTransaction) transaction).getAuthorization();
    }
    throw new IllegalStateException("Payment does not require authorization.");
  }

  public boolean isPendingAuthorization() {
    if (transaction instanceof AuthorizedTransaction) {
      return transaction.isPendingAuthorization();
    }
    throw new IllegalStateException("Payment does not require authorization.");
  }

  public boolean isProcessing() {
    return transaction.isProcessing();
  }

  public boolean isFailed() {
    return transaction.isFailed();
  }

  public boolean isCompleted() {
    return purchase.isCompleted();
  }
}

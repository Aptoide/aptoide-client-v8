package cm.aptoide.pt.v8engine.billing.repository;

import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.product.InAppProduct;
import cm.aptoide.pt.v8engine.billing.product.PaidAppProduct;

public class PaymentRepositoryFactory {

  private final InAppTransactionRepository inAppPaymentConfirmationRepository;
  private final PaidAppTransactionRepository paidAppPaymentConfirmationRepository;

  public PaymentRepositoryFactory(InAppTransactionRepository inAppPaymentConfirmationRepository,
      PaidAppTransactionRepository paidAppPaymentConfirmationRepository) {
    this.inAppPaymentConfirmationRepository = inAppPaymentConfirmationRepository;
    this.paidAppPaymentConfirmationRepository = paidAppPaymentConfirmationRepository;
  }

  public TransactionRepository getPaymentConfirmationRepository(Product product) {
    if (product instanceof InAppProduct) {
      return inAppPaymentConfirmationRepository;
    } else if (product instanceof PaidAppProduct) {
      return paidAppPaymentConfirmationRepository;
    } else {
      throw new IllegalArgumentException("No compatible repository for product " + product.getId());
    }
  }

  public TransactionRepository getPaidAppConfirmationRepository() {
    return paidAppPaymentConfirmationRepository;
  }

  public TransactionRepository getInAppConfirmationRepository() {
    return inAppPaymentConfirmationRepository;
  }
}

package cm.aptoide.pt.v8engine.billing.repository;

import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.product.InAppProduct;
import cm.aptoide.pt.v8engine.billing.product.PaidAppProduct;

public class PaymentRepositoryFactory {

  private final InAppPaymentConfirmationRepository inAppPaymentConfirmationRepository;
  private final PaidAppPaymentConfirmationRepository paidAppPaymentConfirmationRepository;

  public PaymentRepositoryFactory(
      InAppPaymentConfirmationRepository inAppPaymentConfirmationRepository,
      PaidAppPaymentConfirmationRepository paidAppPaymentConfirmationRepository) {
    this.inAppPaymentConfirmationRepository = inAppPaymentConfirmationRepository;
    this.paidAppPaymentConfirmationRepository = paidAppPaymentConfirmationRepository;
  }

  public PaymentConfirmationRepository getPaymentConfirmationRepository(Product product) {
    if (product instanceof InAppProduct) {
      return inAppPaymentConfirmationRepository;
    } else if (product instanceof PaidAppProduct) {
      return paidAppPaymentConfirmationRepository;
    } else {
      throw new IllegalArgumentException("No compatible repository for product " + product.getId());
    }
  }

  public PaymentConfirmationRepository getPaidAppConfirmationRepository() {
    return paidAppPaymentConfirmationRepository;
  }

  public PaymentConfirmationRepository getInAppConfirmationRepository() {
    return inAppPaymentConfirmationRepository;
  }
}

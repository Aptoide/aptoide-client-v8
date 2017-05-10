package cm.aptoide.pt.v8engine.payment.repository;

import cm.aptoide.pt.v8engine.payment.Product;
import cm.aptoide.pt.v8engine.payment.products.InAppBillingProduct;
import cm.aptoide.pt.v8engine.payment.products.PaidAppProduct;

public class ProductRepositoryFactory {

  private PaidAppProductRepository paidAppProductRepository;
  private InAppBillingProductRepository inAppBillingProductRepository;

  public ProductRepositoryFactory(PaidAppProductRepository paidAppProductRepository,
      InAppBillingProductRepository inAppBillingProductRepository) {
    this.paidAppProductRepository = paidAppProductRepository;
    this.inAppBillingProductRepository = inAppBillingProductRepository;
  }

  public PaidAppProductRepository getPaidAppProductRepository() {
    return paidAppProductRepository;
  }

  public InAppBillingProductRepository getInAppProductRepository() {
    return inAppBillingProductRepository;
  }

  public ProductRepository getProductRepository(Product product) {
    if (product instanceof PaidAppProduct) {
      return getPaidAppProductRepository();
    }

    if (product instanceof InAppBillingProduct) {
      return getInAppProductRepository();
    }

    throw new IllegalArgumentException("Invalid product. Must be InApp or Paid App");
  }
}

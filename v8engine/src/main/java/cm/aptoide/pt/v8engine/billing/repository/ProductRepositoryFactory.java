package cm.aptoide.pt.v8engine.billing.repository;

import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.product.InAppProduct;
import cm.aptoide.pt.v8engine.billing.product.PaidAppProduct;

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

    if (product instanceof InAppProduct) {
      return getInAppProductRepository();
    }

    throw new IllegalArgumentException("Invalid product. Must be InApp or Paid App");
  }
}

package cm.aptoide.pt.v8engine.billing.view;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.billing.Payment;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.product.InAppProduct;
import cm.aptoide.pt.v8engine.billing.product.PaidAppProduct;
import cm.aptoide.pt.v8engine.billing.services.BoaCompraPayment;
import cm.aptoide.pt.v8engine.billing.services.PayPalPayment;
import cm.aptoide.pt.v8engine.view.navigator.ActivityNavigator;

public class PaymentNavigator {

  private final ActivityNavigator activityNavigator;

  public PaymentNavigator(ActivityNavigator activityNavigator) {
    this.activityNavigator = activityNavigator;
  }

  public void navigateToAuthorizationView(Payment payment, Product product) {
    if (payment instanceof BoaCompraPayment) {
      activityNavigator.navigateTo(BoaCompraActivity.class, getProductBundle(payment, product));
    } else {
      throw new IllegalArgumentException("Invalid authorized payment.");
    }
  }

  public void navigateToLocalPaymentView(Payment payment, Product product) {
    if (payment instanceof PayPalPayment) {
      activityNavigator.navigateTo(PayPalActivity.class, getProductBundle(payment, product));
    } else {
      throw new IllegalArgumentException("Invalid local payment.");
    }
  }

  private Bundle getProductBundle(Payment payment, Product product) {
    final Bundle bundle;
    if (product instanceof InAppProduct) {
      bundle = ProductActivity.getBundle(payment.getId(), ((InAppProduct) product).getApiVersion(),
          ((InAppProduct) product).getPackageName(), ((InAppProduct) product).getType(),
          ((InAppProduct) product).getSku(), ((InAppProduct) product).getDeveloperPayload());
    } else if (product instanceof PaidAppProduct) {
      bundle = ProductActivity.getBundle(payment.getId(), ((PaidAppProduct) product).getAppId(),
          ((PaidAppProduct) product).getStoreName(), ((PaidAppProduct) product).isSponsored());
    } else {
      throw new IllegalArgumentException("Invalid product. Only in-app and paid apps supported");
    }
    return bundle;
  }
}
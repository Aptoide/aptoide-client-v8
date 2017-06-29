package cm.aptoide.pt.v8engine.billing.view;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.billing.PaymentMethod;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.product.InAppProduct;
import cm.aptoide.pt.v8engine.billing.product.PaidAppProduct;
import cm.aptoide.pt.v8engine.billing.methods.BoaCompraPaymentMethod;
import cm.aptoide.pt.v8engine.billing.methods.PayPalPaymentMethod;
import cm.aptoide.pt.v8engine.view.navigator.ActivityNavigator;

public class PaymentNavigator {

  private final ActivityNavigator activityNavigator;

  public PaymentNavigator(ActivityNavigator activityNavigator) {
    this.activityNavigator = activityNavigator;
  }

  public void navigateToAuthorizationView(PaymentMethod paymentMethod, Product product) {
    if (paymentMethod instanceof BoaCompraPaymentMethod) {
      activityNavigator.navigateTo(BoaCompraActivity.class,
          getProductBundle(paymentMethod, product));
    } else {
      throw new IllegalArgumentException("Invalid authorized payment.");
    }
  }

  public void navigateToLocalPaymentView(PaymentMethod paymentMethod, Product product) {
    if (paymentMethod instanceof PayPalPaymentMethod) {
      activityNavigator.navigateTo(PayPalActivity.class, getProductBundle(paymentMethod, product));
    } else {
      throw new IllegalArgumentException("Invalid local payment.");
    }
  }

  private Bundle getProductBundle(PaymentMethod paymentMethod, Product product) {
    final Bundle bundle;
    if (product instanceof InAppProduct) {
      bundle =
          ProductActivity.getBundle(paymentMethod.getId(), ((InAppProduct) product).getApiVersion(),
              ((InAppProduct) product).getPackageName(), ((InAppProduct) product).getType(),
              ((InAppProduct) product).getSku(), ((InAppProduct) product).getDeveloperPayload());
    } else if (product instanceof PaidAppProduct) {
      bundle =
          ProductActivity.getBundle(paymentMethod.getId(), ((PaidAppProduct) product).getAppId(),
              ((PaidAppProduct) product).getStoreName(), ((PaidAppProduct) product).isSponsored());
    } else {
      throw new IllegalArgumentException("Invalid product. Only in-app and paid apps supported");
    }
    return bundle;
  }
}
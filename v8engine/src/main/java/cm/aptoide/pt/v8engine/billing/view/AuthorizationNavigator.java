package cm.aptoide.pt.v8engine.billing.view;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.billing.Payment;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.product.InAppProduct;
import cm.aptoide.pt.v8engine.billing.product.PaidAppProduct;
import cm.aptoide.pt.v8engine.billing.services.WebPayment;
import cm.aptoide.pt.v8engine.billing.services.PayPalPayment;
import cm.aptoide.pt.v8engine.view.navigator.ActivityNavigator;

public class AuthorizationNavigator {

  private final ActivityNavigator activityNavigator;

  public AuthorizationNavigator(ActivityNavigator activityNavigator) {
    this.activityNavigator = activityNavigator;
  }

  public void navigateToAuthorizationView(Payment payment, Product product) {

    final Bundle bundle;

    if (product instanceof InAppProduct) {
      bundle = WebAuthorizationActivity.getBundle(payment.getId(),
          ((InAppProduct) product).getApiVersion(), ((InAppProduct) product).getPackageName(),
          ((InAppProduct) product).getType(), ((InAppProduct) product).getSku(),
          ((InAppProduct) product).getDeveloperPayload());
    } else if (product instanceof PaidAppProduct) {
      bundle =
          WebAuthorizationActivity.getBundle(payment.getId(), ((PaidAppProduct) product).getAppId(),
              ((PaidAppProduct) product).getStoreName(), ((PaidAppProduct) product).isSponsored());
    } else {
      throw new IllegalArgumentException("Invalid product. Only in app and paid apps supported");
    }

    if (payment instanceof PayPalPayment) {
      activityNavigator.navigateTo(PayPalAuthorizationActivity.class, bundle);
    } else if (payment instanceof WebPayment) {
      activityNavigator.navigateTo(WebAuthorizationActivity.class, bundle);
    } else {
      throw new IllegalArgumentException("Invalid payment.");
    }
  }
}
package cm.aptoide.pt.v8engine.billing;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.analytics.Event;
import cm.aptoide.pt.v8engine.analytics.events.FacebookEvent;
import cm.aptoide.pt.v8engine.billing.product.AbstractProduct;
import cm.aptoide.pt.v8engine.billing.product.InAppProduct;
import com.facebook.appevents.AppEventsLogger;

public class BillingAnalytics {

  private final Analytics analytics;
  private final AppEventsLogger facebook;
  private final String aptoidePackageName;

  public BillingAnalytics(Analytics analytics, AppEventsLogger facebook,
      String aptoidePackageName) {
    this.analytics = analytics;
    this.facebook = facebook;
    this.aptoidePackageName = aptoidePackageName;
  }

  public void sendPaymentViewShowEvent() {
    analytics.sendEvent(getFacebookPaymentEvent("Payment_Pop_Up", "Show", new Bundle()));
  }

  public void sendPaymentViewCancelEvent(Product product) {
    analytics.sendEvent(
        getFacebookPaymentEvent("Payment_Pop_Up", "Cancel", getProductBundle(product)));
  }

  public void sendPaymentViewBuyEvent(Product product, String paymentMethodName) {
    final Bundle bundle = getProductBundle(product);
    bundle.putString("payment_method", paymentMethodName);
    analytics.sendEvent(getFacebookPaymentEvent("Payment_Pop_Up", "Buy", bundle));
  }

  public void sendPaymentSuccessEvent() {
    analytics.sendEvent(getFacebookPaymentEvent("Payment_Pop_Up", "Success", new Bundle()));
  }

  public void sendPaymentErrorEvent() {
    analytics.sendEvent(getFacebookPaymentEvent("Payment_Pop_Up", "Error", new Bundle()));
  }

  public void sendPayerAuthenticatedEvent(boolean payerAuthenticated) {
    if (payerAuthenticated) {
      analytics.sendEvent(getFacebookPaymentEvent("Payment_Login", "Success", new Bundle()));
    }
  }

  public void sendPayerAuthenticationResultEvent(boolean payerAuthenticated) {
    final String action;
    if (payerAuthenticated) {
      action = "Success";
    } else {
      action = "Cancel";
    }
    analytics.sendEvent(getFacebookPaymentEvent("Payment_Login", action, new Bundle()));
  }

  public void sendAuthorizationSuccessEvent(String paymentMethodName) {
    final Bundle bundle = new Bundle();
    bundle.putString("payment_method", paymentMethodName);
    analytics.sendEvent(getFacebookPaymentEvent("Payment_Authorization_Page", "Success", bundle));
  }

  public void sendAuthorizationCancelEvent(String paymentMethodName) {
    final Bundle bundle = new Bundle();
    bundle.putString("payment_method", paymentMethodName);
    analytics.sendEvent(getFacebookPaymentEvent("Payment_Authorization_Page", "Cancel", bundle));
  }

  public void sendAuthorizationErrorEvent(String paymentMethodName) {
    final Bundle bundle = new Bundle();
    bundle.putString("payment_method", paymentMethodName);
    analytics.sendEvent(getFacebookPaymentEvent("Payment_Authorization_Page", "Error", bundle));
  }

  private Event getFacebookPaymentEvent(String eventName, String action, Bundle bundle) {
    bundle.putString("action", action);
    return new FacebookEvent(facebook, eventName, bundle);
  }

  private Bundle getProductBundle(Product product) {

    final String packageName;
    if (product instanceof InAppProduct) {
      packageName = ((InAppProduct) product).getPackageName();
    } else {
      packageName = aptoidePackageName;
    }

    final Bundle bundle = new Bundle();
    bundle.putDouble("purchase_value", product.getPrice()
        .getAmount());
    bundle.putString("purchase_currency", product.getPrice()
        .getCurrency());
    bundle.putString("package_name_seller", packageName);
    bundle.putInt("package_version_code_seller",
        ((AbstractProduct) product).getPackageVersionCode());
    return bundle;
  }
}

package cm.aptoide.pt.billing;

import android.os.Bundle;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.analytics.Event;
import cm.aptoide.pt.analytics.events.FacebookEvent;
import cm.aptoide.pt.billing.product.Product;
import cm.aptoide.pt.billing.product.InAppProduct;
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

  public void sendPaymentViewBuyEvent(Product product, String serviceName) {
    final Bundle bundle = getProductBundle(product);
    bundle.putString("payment_method", serviceName);
    analytics.sendEvent(getFacebookPaymentEvent("Payment_Pop_Up", "Buy", bundle));
  }

  public void sendPaymentSuccessEvent() {
    analytics.sendEvent(getFacebookPaymentEvent("Payment_Pop_Up", "Success", new Bundle()));
  }

  public void sendPaymentErrorEvent() {
    analytics.sendEvent(getFacebookPaymentEvent("Payment_Pop_Up", "Error", new Bundle()));
  }

  public void sendCustomerAuthenticatedEvent(boolean customerAuthenticated) {
    if (customerAuthenticated) {
      analytics.sendEvent(getFacebookPaymentEvent("Payment_Login", "Success", new Bundle()));
    }
  }

  public void sendCustomerAuthenticationResultEvent(boolean customerAuthenticated) {
    final String action;
    if (customerAuthenticated) {
      action = "Success";
    } else {
      action = "Cancel";
    }
    analytics.sendEvent(getFacebookPaymentEvent("Payment_Login", action, new Bundle()));
  }

  public void sendAuthorizationSuccessEvent(String serviceName) {
    final Bundle bundle = new Bundle();
    bundle.putString("payment_method", serviceName);
    analytics.sendEvent(getFacebookPaymentEvent("Payment_Authorization_Page", "Success", bundle));
  }

  public void sendAuthorizationCancelEvent(String serviceName) {
    final Bundle bundle = new Bundle();
    bundle.putString("payment_method", serviceName);
    analytics.sendEvent(getFacebookPaymentEvent("Payment_Authorization_Page", "Cancel", bundle));
  }

  public void sendAuthorizationErrorEvent(String serviceName) {
    final Bundle bundle = new Bundle();
    bundle.putString("payment_method", serviceName);
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
    bundle.putInt("package_version_code_seller", ((Product) product).getPackageVersionCode());
    return bundle;
  }
}

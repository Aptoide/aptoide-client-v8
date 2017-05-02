package cm.aptoide.pt.v8engine.payment;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.analytics.Event;
import cm.aptoide.pt.v8engine.analytics.events.FacebookEvent;
import com.facebook.appevents.AppEventsLogger;

public class PaymentAnalytics {

  private final Analytics analytics;
  private final AppEventsLogger facebook;
  private final String aptoidePackageName;

  public PaymentAnalytics(Analytics analytics, AppEventsLogger facebook,
      String aptoidePackageName) {
    this.analytics = analytics;
    this.facebook = facebook;
    this.aptoidePackageName = aptoidePackageName;
  }

  public void sendPaidAppBuyButtonPressedEvent(Product product) {
    analytics.sendEvent(new FacebookEvent(facebook, "Clicked_On_Buy_Button",
        getProductBundle(product)));
  }

  public void sendPaymentCancelButtonPressedEvent(Product product, Payment payment) {
    analytics.sendEvent(getPaymentEvent(product, payment, "Cancel"));
  }

  public void sendPaymentBuyButtonPressedEvent(Product product, Payment payment) {
    analytics.sendEvent(getPaymentEvent(product, payment, "Buy"));
  }

  private Event getPaymentEvent(Product product, Payment payment, String action) {
    final Bundle bundle = getProductBundle(product);
    bundle.putString("action", action);
    bundle.putString("payment_method", payment.getName());
    return new FacebookEvent(facebook, "Payment_Pop_Up", bundle);
  }

  private Bundle getProductBundle(Product product) {
    final Bundle bundle = new Bundle();
    bundle.putDouble("purchase_value", product.getPrice().getAmount());
    bundle.putString("purchase_currency", product.getPrice().getCurrency());
    bundle.putString("package_name_seller", aptoidePackageName);
    return bundle;
  }
}
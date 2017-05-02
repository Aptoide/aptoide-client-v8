package cm.aptoide.pt.v8engine.payment;

import android.os.Bundle;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.analytics.events.FacebookEvent;
import com.facebook.appevents.AppEventsLogger;

public class PaymentAnalytics {

  private final Analytics analytics;
  private final AppEventsLogger facebook;
  private final String aptoidePackageName;

  public PaymentAnalytics(Analytics analytics, AppEventsLogger facebook, String
      aptoidePackageName) {
    this.analytics = analytics;
    this.facebook = facebook;
    this.aptoidePackageName = aptoidePackageName;
  }

  public void sendPaidAppBuyButtonPressedEvent(Product product) {
    final Bundle bundle = new Bundle();
    bundle.putDouble("purchase_value", product.getPrice().getAmount());
    bundle.putString("purchase_currency", product.getPrice().getCurrency());
    bundle.putString("package_name_seller", aptoidePackageName);
    analytics.sendEvent(new FacebookEvent(facebook, "Clicked_On_Buy_Button", bundle));
  }

  public void sendPaymentCancelButtonPressedEvent(Product product, Payment payment) {
    final Bundle bundle = new Bundle();
    bundle.putString("action", "Cancel");
    bundle.putString("payment_method", payment.getName());
    bundle.putDouble("purchase_value", product.getPrice().getAmount());
    bundle.putString("purchase_currency", product.getPrice().getCurrency());
    bundle.putString("package_name_seller", aptoidePackageName);
    analytics.sendEvent(new FacebookEvent(facebook, "Payment_Pop_Up", bundle));
  }
}
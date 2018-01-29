package cm.aptoide.pt.billing;

import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import cm.aptoide.pt.billing.payment.Payment;
import cm.aptoide.pt.billing.product.InAppProduct;
import cm.aptoide.pt.billing.product.Product;
import java.util.HashMap;
import java.util.Map;

public class BillingAnalytics {

  public final static String PAYMENT_POPUP = "Payment_Pop_Up";
  public final static String PAYMENT_LOGIN = "Payment_Login";
  public final static String PAYMENT_AUTH = "Payment_Authorization_Page";
  private final String aptoidePackageName;
  private final AnalyticsManager analyticsManager;
  private final NavigationTracker navigationTracker;

  public BillingAnalytics(String aptoidePackageName, AnalyticsManager analyticsManager,
      NavigationTracker navigationTracker) {
    this.aptoidePackageName = aptoidePackageName;
    this.analyticsManager = analyticsManager;
    this.navigationTracker = navigationTracker;
  }

  public void sendPaymentViewShowEvent() {
    Map<String, Object> map = new HashMap<>();
    map.put("action", "Show");
    analyticsManager.logEvent(map, PAYMENT_POPUP, AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void sendPaymentViewCancelEvent(Payment payment) {
    Map<String, Object> map = getProductMap(payment.getProduct());
    map.put("action", "Cancel");
    analyticsManager.logEvent(map, PAYMENT_POPUP, AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void sendPaymentViewBuyEvent(Payment payment) {
    Map<String, Object> map = getProductMap(payment.getProduct());
    map.put("payment_method", payment.getSelectedPaymentService()
        .getType());
    map.put("action", "Buy");
    analyticsManager.logEvent(map, PAYMENT_POPUP, AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void sendPaymentSuccessEvent() {
    Map<String, Object> map = new HashMap<>();
    map.put("action", "Success");
    analyticsManager.logEvent(map, PAYMENT_POPUP, AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void sendPaymentErrorEvent() {
    Map<String, Object> map = new HashMap<>();
    map.put("action", "Error");
    analyticsManager.logEvent(map, PAYMENT_POPUP, AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void sendCustomerAuthenticatedEvent(boolean customerAuthenticated) {
    if (customerAuthenticated) {
      Map<String, Object> map = new HashMap<>();
      map.put("action", "Success");
      analyticsManager.logEvent(map, PAYMENT_LOGIN, AnalyticsManager.Action.CLICK,
          getViewName(true));
    }
  }

  public void sendCustomerAuthenticationResultEvent(boolean customerAuthenticated) {
    final String action;
    if (customerAuthenticated) {
      action = "Success";
    } else {
      action = "Cancel";
    }
    Map<String, Object> map = new HashMap<>();
    map.put("action", action);
    analyticsManager.logEvent(map, PAYMENT_LOGIN, AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void sendAuthorizationSuccessEvent(Payment payment) {
    final Map<String, Object> map = new HashMap<>();
    map.put("payment_method", payment.getSelectedPaymentService()
        .getType());
    map.put("action", "Success");
    analyticsManager.logEvent(map, PAYMENT_AUTH, AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void sendAuthorizationCancelEvent(String serviceName) {
    final Map<String, Object> map = new HashMap<>();
    map.put("payment_method", serviceName);
    map.put("action", "Cancel");
    analyticsManager.logEvent(map, PAYMENT_AUTH, AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void sendAuthorizationErrorEvent(String serviceName) {
    final Map<String, Object> map = new HashMap<>();
    map.put("payment_method", serviceName);
    map.put("action", "Error");
    analyticsManager.logEvent(map, PAYMENT_AUTH, AnalyticsManager.Action.CLICK, getViewName(true));
  }

  private Map<String, Object> getProductMap(Product product) {

    final String packageName;
    if (product instanceof InAppProduct) {
      packageName = ((InAppProduct) product).getPackageName();
    } else {
      packageName = aptoidePackageName;
    }

    final Map<String, Object> map = new HashMap<>();
    map.put("purchase_value", String.valueOf(product.getPrice()
        .getAmount()));
    map.put("purchase_currency", product.getPrice()
        .getCurrency());
    map.put("package_name_seller", packageName);
    map.put("package_version_code_seller",
        String.valueOf(((Product) product).getPackageVersionCode()));
    return map;
  }

  private String getViewName(boolean isCurrent) {
    return navigationTracker.getViewName(isCurrent, "Billing");
  }
}
